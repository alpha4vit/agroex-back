package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.filter.FilterService;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.*;
import com.vention.agroex.util.constant.StatusConstants;
import com.vention.agroex.util.mapper.LotMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private final ImageServiceStorage imageServiceStorage;
    private final ProductCategoryService productCategoryService;

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
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
        tagService.saveList(lotEntity.getTags());

        lotEntity.setUser(userEntity);
        lotEntity.getLocation().setCountry(countryEntity);
        lotEntity.setProductCategory(productCategoryEntity);

        if (lotEntity.getLotType().equals("auctionSell")) {
            lotEntity.setAdminStatus(StatusConstants.NEW);
        }

        var saved = lotRepository.save(lotEntity);
        saved.setImages(uploadImages(saved.getId(), files));

        return saved;
    }

    private void validateFields(LotEntity lotEntity, MultipartFile[] files) {
        if (files == null || files.length < 1 || files.length > 6)
            throw new ImageLotException("Incorrect quantity of images must be from 1 to 6!");
        if (lotEntity.getLotType().equals("auctionSell")) {
            if (lotEntity.getDuration() == null) {
                throw new InvalidArgumentException("Duration of auction lot should not be null");
            }
            if (lotEntity.getDuration() < 36000000L) {
                throw new InvalidArgumentException("Duration must be more than 10 minutes");
            }
            if (lotEntity.getMinPrice() >= lotEntity.getPrice()) {
                throw new InvalidArgumentException("Min price can`t be less than lot price");
            }
        }
    }

    @Override
    public LotEntity getById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no such lot with id: " + id));
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
    public LotEntity update(Long id, LotEntity entity, MultipartFile[] files) {
        clearImagesForLot(id);

        var result = lotMapper.update(getById(id), entity);

        var userEntity = userService.getById(result.getUser().getId());
        var countryEntity = countryService.getById(result.getLocation().getCountry().getId());

        var productCategoryEntity = switch (entity.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(entity.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null ->
                    productCategoryService.save(entity.getProductCategory());
            case null, default ->
                    throw new InvalidArgumentException("Provide productCategory.id or productCategory.title");
        };

        result.setUser(userEntity);
        result.getLocation().setCountry(countryEntity);
        result.setProductCategory(productCategoryEntity);

        var saved = lotRepository.save(result);

        if (files != null && files.length >= 1 && !(files.length > 6))
            saved.setImages(uploadImages(saved.getId(), files));
        else
            throw new ImageLotException("Incorrect quantity of images must be from 1 to 6!");

        return saved;
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
    public List<ImageEntity> uploadImages(Long id, MultipartFile[] files) {
        var lotEntity = getById(id);
        List<ImageEntity> images = new ArrayList<>();
        try {
            Arrays.stream(files).forEach(file -> {
                String imageName = imageServiceStorage.uploadToStorage(new Image(file));
                images.add(imageService.save(ImageEntity.builder()
                        .lot(lotEntity)
                        .name(imageName)
                        .build()));
            });
            return images;
        } catch (ImageException e) {
            images.forEach(imageServiceStorage::remove);
            throw new ImageLotException(id, e.getMessage());
        }
    }

    @Override
    public void deleteImage(String fileName) {
        var imageEntity = imageService.getByName(fileName);
        imageService.delete(imageEntity);
    }

    public void clearImagesForLot(Long lotId) {
        var lotEntity = getById(lotId);
        lotEntity.getImages().forEach(image -> {
            imageService.delete(image);
            imageServiceStorage.remove(image);
        });
    }

    @Override
    public List<LotEntity> getWithCriteria(Map<String, String> filters) {
        return filterService.getWithCriteria(filters);
    }
}
