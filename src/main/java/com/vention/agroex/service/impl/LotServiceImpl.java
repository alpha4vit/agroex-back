package com.vention.agroex.service.impl;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.exception.LotEditException;
import com.vention.agroex.filter.FilterService;
import com.vention.agroex.model.LotRejectRequest;
import com.vention.agroex.model.LotStatusResponse;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.*;
import com.vention.agroex.util.constant.LotTypeConstants;
import com.vention.agroex.util.constant.StatusConstants;
import com.vention.agroex.util.mapper.LotMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final ImageServiceStorage imageServiceStorage;
    private final ImageService imageService;
    private final FilterService filterService;
    private final ProductCategoryService productCategoryService;
    private final CountryService countryService;
    private final UserService userService;
    private final LotMapper lotMapper;
    private final TagService tagService;
    private final CurrencyRateService currencyRateService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public LotEntity save(LotEntity lotEntity, MultipartFile[] files) {
        validateFields(lotEntity, files);

        var userEntity = userService.getById(lotEntity.getUser().getId());
        var countryEntity = countryService.getById(lotEntity.getLocation().getCountry().getId());

        var productCategoryEntity = switch (lotEntity.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(lotEntity.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null ->
                    productCategoryService.save(lotEntity.getProductCategory());
            case null, default ->
                    throw new InvalidArgumentException("Provide productCategory.id or productCategory.title");
        };

        lotEntity.setUser(userEntity);
        lotEntity.getLocation().setCountry(countryEntity);
        lotEntity.setProductCategory(productCategoryEntity);
        lotEntity.setTags(lotEntity.getTags()
                .stream().map(tagService::save)
                .toList());

        if (lotEntity.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            lotEntity.setInnerStatus(StatusConstants.NEW);
            lotEntity.setStatus(StatusConstants.INACTIVE);
        }

        var saved = lotRepository.save(lotEntity);
        saved.setImages(imageService.uploadImages(saved, files));

        return saved;
    }

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
    public LotEntity save(LotEntity lotEntity, MultipartFile[] files, String currency) {
        var saved = save(lotEntity, files);
        return updatePrice(saved, currency);
    }

    private void validateFields(LotEntity lotEntity, MultipartFile[] files) {
        if (files == null || files.length < 1 || files.length > 6)
            throw new InvalidArgumentException(Map.of("images", "Incorrect quantity of images must be from 1 to 6!"), "Invalid arguments!");
    }

    @Override
    public LotEntity getById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no such lot with id: " + id));
    }

    @Override
    public LotEntity getById(Long id, String currency) {
        return updatePrice(getById(id), currency);
    }

    @Override
    public void deleteById(Long id) {
        lotRepository.deleteById(id);
    }

    @Override
    public void delete(LotEntity lot) {
        lotRepository.delete(lot);
    }

    @Override
    public List<LotEntity> getAll() {
        return lotRepository.findAll();
    }

    @Override
    public List<LotEntity> getAll(String currency) {
        return null;
    }

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
    public LotEntity update(Long id, LotEntity entity, MultipartFile[] files) {
        var lotToUpdate = getById(id);

        imageService.updateImagesForLot(lotToUpdate, updatedLot, files);
        if (lotToUpdate.getInnerStatus().equals(StatusConstants.ON_MODERATION)) {
            throw new LotEditException("You can`t edit this lot while moderation");
        }
        if (lotToUpdate.getInnerStatus().equals(StatusConstants.FINISHED)) {
            throw new LotEditException("This auction has already ended");
        }
        var updatedLot = lotMapper.update(lotToUpdate, entity);
        imageService.updateImagesForLot(lotToUpdate, updatedLot, files);

        var userEntity = userService.getById(updatedLot.getUser().getId());
        var countryEntity = countryService.getById(updatedLot.getLocation().getCountry().getId());

        var productCategoryEntity = switch (entity.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(entity.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null ->
                    productCategoryService.save(entity.getProductCategory());
            case null, default ->
                    throw new InvalidArgumentException("Provide productCategory.id or productCategory.title");
        };

        updatedLot.setUser(userEntity);
        updatedLot.getLocation().setCountry(countryEntity);
        updatedLot.setProductCategory(productCategoryEntity);
        updatedLot.setTags(updatedLot.getTags()
                .stream().map(tagService::save)
                .toList());

        return lotRepository.save(updatedLot);
    }

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
    public LotEntity update(Long id, LotEntity entity, MultipartFile[] files, String currency) {
        var updated = update(id, entity, files);
        return updatePrice(updated, currency);
    }

    @Override
    public LotEntity update(Long id, LotEntity entity) {
        var result = lotMapper.update(getById(id), entity);

        var userEntity = userService.getById(result.getUser().getId());
        var countryEntity = countryService.getById(result.getLocation().getCountry().getId());
        var productCategoryEntity = productCategoryService.getById(result.getProductCategory().getId());

        result.setUser(userEntity);
        result.getLocation().setCountry(countryEntity);
        result.setProductCategory(productCategoryEntity);

        return lotRepository.save(result);
    }

    @Override
    public void deleteImage(String fileName) {
        var imageEntity = imageService.getByName(fileName);
        imageService.delete(imageEntity);
    }

    @Override
    public LotStatusResponse getLotStatus(Long id) {
        var lot = getById(id);
        if (!lot.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            throw new InvalidArgumentException("This lot is not an auction lot");
        }
        var bets = lot.getBets();
        var statusBuilder = LotStatusResponse.builder()
                .lotId(id)
                .userid(lot.getUser().getId())
                .status(lot.getStatus());
        if (!bets.isEmpty()) {
            statusBuilder
                    .betAmount(bets.getFirst().getAmount())
                    .betTime(bets.getFirst().getBetTime());
        }
        return statusBuilder.build();
    }

    @Override
    public LotEntity putOnModeration(Long id) {
        var lot = getById(id);
        if (!lot.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            throw new InvalidArgumentException("This lot is not an auction lot");
        }
        lot.setInnerStatus(StatusConstants.ON_MODERATION);
        return update(id, lot);
    }

    @Override
    public LotEntity putOnModeration(Long lotId, String currency) {
        var lot = putOnModeration(lotId);
        return updatePrice(lot, currency);
    }

    @Override
    public LotEntity approve(Long id) {
        var lot = getById(id);
        if (!lot.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            throw new InvalidArgumentException("This lot is not an auction lot");
        }
        lot.setExpirationDate(Instant.now().plusMillis(lot.getDuration()));
        lot.setInnerStatus(StatusConstants.APPROVED);
        lot.setStatus(StatusConstants.ACTIVE);
        return update(id, lot);
    }

    @Override
    public void finishAuction(LotEntity lot) {
        lot.setStatus(StatusConstants.FINISHED);
        update(lot.getId(), lot);
    }

    @Override
    public LotEntity approve(Long lotId, String currency) {
        var lot = approve(lotId);
        return updatePrice(lot, currency);
    }

    @Override
    public LotEntity reject(LotRejectRequest rejectRequest) {
        var lot = getById(rejectRequest.lotId());
        lot.setInnerStatus(StatusConstants.REJECTED_BY_ADMIN);
        lot.setStatus(StatusConstants.INACTIVE);
        lot.setAdminComment(rejectRequest.adminComment() == null ? "" : rejectRequest.adminComment());
        return update(rejectRequest.lotId(), lot);
    }

    @Override
    public LotEntity changeUserStatus(Long id, boolean status) {
        var lot = getById(id);
        lot.setUserStatus(status ? StatusConstants.ACTIVE : StatusConstants.INACTIVE);

        return update(id, lot);
    }

    @Override
    public List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize) {
        var searchCriteria = filterService.getCriteria(filters);
        return lotRepository.findAll(searchCriteria, PageRequest.of(pageNumber, pageSize)).toList();
    }

    @Override
    public List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize, String currency) {
        var lots = getWithCriteria(filters, pageNumber, pageSize);
        return lots.stream().map(lot -> updatePrice(lot, currency)).toList();
    }

    private LotEntity updatePrice(LotEntity lotEntity, String currency) {
        if (!lotEntity.getOriginalCurrency().equals(currency)) {
            var currencyRate = currencyRateService.getByCurrencies(lotEntity.getOriginalCurrency(), currency);
            lotEntity.updatePrice(currencyRate);
        }
        return lotEntity;
    }
}
