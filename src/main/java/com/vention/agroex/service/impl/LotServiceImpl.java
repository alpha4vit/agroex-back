package com.vention.agroex.service.impl;

import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.entity.CurrencyRateEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.exception.InvalidBetException;
import com.vention.agroex.exception.LotEditException;
import com.vention.agroex.filter.FilterService;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotMapper lotMapper;
    private final TagService tagService;
    private final UserService userService;
    private final ImageService imageService;
    private final FilterService filterService;
    private final LotRepository lotRepository;
    private final CountryService countryService;
    private final CurrencyRateService currencyRateService;
    private final ProductCategoryService productCategoryService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public LotEntity save(LotEntity lotEntity, MultipartFile[] files) {
        validateFiles(files);

        var countryEntity = countryService.getById(lotEntity.getLocation().getCountry().getId());

        var productCategoryEntity = switch (lotEntity.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(lotEntity.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null ->
                    productCategoryService.save(lotEntity.getProductCategory());
            case null, default ->
                    throw new InvalidArgumentException("Provide productCategory.id or productCategory.title");
        };

        lotEntity.setUser(userService.getAuthenticatedUser());
        lotEntity.getLocation().setCountry(countryEntity);
        lotEntity.setProductCategory(productCategoryEntity);
        lotEntity.setTags(lotEntity.getTags()
                .stream().map(tagService::save)
                .toList());

        if (lotEntity.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            lotEntity.setInnerStatus(StatusConstants.NEW);
            lotEntity.setStatus(StatusConstants.INACTIVE);
        }

        if (lotEntity.getExpirationDate() != null) {
            lotEntity.setExpirationDate(
                    lotEntity.getExpirationDate()
                            .withZoneSameLocal(lotEntity.getUser().getZoneinfo())
            );
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

    private void validateFiles(MultipartFile[] files) {
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
        var lotToDelete = getById(id);
        if (lotToDelete.getBets() != null) {
            throw new LotEditException("You can`t delete this lot, it already has customers");
        }
        if (lotToDelete.getStatus().equals(StatusConstants.ACTIVE)) {
            throw new LotEditException("You can`t delete this lot, deactivate it firstly");
        }
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
    public LotEntity update(Long id, LotEntity entity, MultipartFile[] files, String currency) {
        var lotToUpdate = getById(id);

        imageService.updateImagesForLot(lotToUpdate, lotToUpdate, files);
        if (lotToUpdate.getInnerStatus().equals(StatusConstants.ON_MODERATION)) {
            throw new LotEditException("You can`t edit this lot while moderation");
        }
        if (lotToUpdate.getInnerStatus().equals(StatusConstants.FINISHED)) {
            throw new LotEditException("This auction has already ended");
        }
        var updatedLot = lotMapper.update(lotToUpdate, entity);
        imageService.updateImagesForLot(lotToUpdate, updatedLot, files);

        var countryEntity = countryService.getById(updatedLot.getLocation().getCountry().getId());

        var productCategoryEntity = switch (entity.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(entity.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null ->
                    productCategoryService.save(entity.getProductCategory());
            case null, default ->
                    throw new InvalidArgumentException("Provide productCategory.id or productCategory.title");
        };

        if (!lotToUpdate.getInnerStatus().equals(StatusConstants.APPROVED)) {
            lotToUpdate.setInnerStatus(StatusConstants.NEW);
        }
        updatedLot.setUser(userService.getAuthenticatedUser());
        updatedLot.getLocation().setCountry(countryEntity);
        updatedLot.setProductCategory(productCategoryEntity);
        updatedLot.setTags(updatedLot.getTags()
                .stream().map(tagService::save)
                .toList());

        return updatePrice(lotRepository.save(updatedLot), currency);
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

    private LotEntity moderateLot(Long id, String adminComment) {
        var lot = getById(id);
        if (!lot.getUser().getEnabled())
            throw new InvalidArgumentException("Lot owner is disabled!");
        if (adminComment != null) {
            lot.setAdminComment(adminComment);
        }
        lot.setInnerStatus(StatusConstants.ON_MODERATION);
        return update(id, lot);
    }

    @Override
    public LotEntity putOnModeration(Long lotId, String currency, String adminComment) {
        var lot = moderateLot(lotId, adminComment);
        return updatePrice(lot, currency);
    }


    private LotEntity approveLot(Long id, String adminComment) {
        var lot = getById(id);
        if (!lot.getUser().getEnabled())
            throw new InvalidArgumentException("Lot owner is disabled!");
        if (!lot.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            throw new InvalidArgumentException("This lot is not an auction lot");
        }
        lot.setExpirationDate(Instant.now().plusMillis(lot.getDuration()).atZone(lot.getUser().getZoneinfo()));
        lot.setActualStartDate(ZonedDateTime.now(lot.getUser().getZoneinfo()));
        lot.setInnerStatus(StatusConstants.APPROVED);
        if (adminComment != null) {
            lot.setAdminComment(adminComment);
        }
        lot.setStatus(StatusConstants.ACTIVE);
        return update(id, lot);
    }

    @Override
    public LotEntity approve(Long lotId, String currency, String adminComment) {
        var lot = approveLot(lotId, adminComment);
        return updatePrice(lot, currency);
    }

    @Override
    public void finishAuction(LotEntity lot) {
        lot.setStatus(StatusConstants.FINISHED);
        update(lot.getId(), lot);
    }

    @Override
    public LotEntity makeDeal(Long lotId, UUID userId, String currency) {
        var lot = getById(lotId);
        validateDeal(lot, userId);
        var bets = lot.getBets();
        var newBetEntity = BetEntity.builder()
                .amount(lot.getOriginalPrice())
                .betTime(Instant.now())
                .user(userService.getById(userId))
                .lot(getById(lotId))
                .build();

        bets.add(newBetEntity);
        lot.setBets(bets);
        var updated = update(lot.getId(), lot);
        return updatePrice(updated, currency);
    }

    private void validateDeal(LotEntity lot, UUID userId) {
        if (lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            throw new InvalidBetException("This lot is an auction lot, you can`t buy it");
        }
        if (lot.getUser().getId() == userId) {
            throw new InvalidBetException("You can't buy/sell your own lot");
        }
        if (!lot.getStatus().equals(StatusConstants.ACTIVE)) {
            throw new InvalidBetException("This lot is not active");
        }
    }

    @Override
    public LotEntity reject(Long lotId, String adminComment) {
        if (adminComment.isEmpty()) {
            throw new LotEditException("You need to give comment about rejection");
        }
        var lot = getById(lotId);
        lot.setInnerStatus(StatusConstants.REJECTED_BY_ADMIN);
        lot.setStatus(StatusConstants.INACTIVE);
        lot.setAdminComment(adminComment);
        return update(lotId, lot);
    }

    @Override
    public LotEntity changeUserStatus(Long id, boolean status) {
        var lot = getById(id);
        validateUserStatusChangeRequest(lot);
        var userStatus = status ? StatusConstants.ACTIVE : StatusConstants.INACTIVE;
        var lotStatus = status ? StatusConstants.ACTIVE : StatusConstants.INACTIVE;
        if (lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            lot.setInnerStatus(StatusConstants.NEW);
            lotStatus = StatusConstants.INACTIVE;
        }
        lot.setUserStatus(userStatus);
        lot.setStatus(lotStatus);
        return update(id, lot);
    }

    private void validateUserStatusChangeRequest(LotEntity lot) {
        if (lot.getLotType().equals(StatusConstants.AUCTION_SELL) &&
                !lot.getInnerStatus().equals(StatusConstants.NEW)) {
            throw new LotEditException("It`s not possible to change lot userStatus already");
        }
        if (!lot.getBets().isEmpty()) {
            throw new LotEditException("It`s not possible to change lot userStatus. It already has customers");
        }
    }

    @Override
    public List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize, String currency) {
        var searchCriteria = filterService.getCriteria(filters);

        var lots = lotRepository.findAll(searchCriteria, PageRequest.of(pageNumber, pageSize)).toList();
        var lotsWithUpdatedPrice = new ArrayList<>(lots.stream().map(lot -> updatePrice(lot, currency)).toList());
        var nonNullFilters = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        nonNullFilters.forEach((field, value) -> {
            switch (field) {
                case "minPrice" ->
                        lotsWithUpdatedPrice.removeIf(lotEntity -> lotEntity.getPrice().compareTo(new BigDecimal(value)) < 0);
                case "maxPrice" ->
                        lotsWithUpdatedPrice.removeIf(lotEntity -> lotEntity.getPrice().compareTo(new BigDecimal(value)) > 0);
            }
        });

        return lotsWithUpdatedPrice;
    }

    private LotEntity updatePrice(LotEntity lotEntity, String currency) {
        if (!lotEntity.getOriginalCurrency().equals(currency)) {
            var currencyRate = currencyRateService.getByCurrencies(lotEntity.getOriginalCurrency(), currency);
            lotEntity.updatePrice(currencyRate);
        } else
            lotEntity.updatePrice(new CurrencyRateEntity(lotEntity.getOriginalCurrency()));
        return lotEntity;
    }

    @Override
    public List<LotEntity> getUserActivityById(UUID id, String currency) {
        var user = userService.getById(id);
        return lotRepository.findByBetsUserId(user.getId()).stream()
                .map(lot -> updatePrice(lot, currency))
                .toList();
    }
}
