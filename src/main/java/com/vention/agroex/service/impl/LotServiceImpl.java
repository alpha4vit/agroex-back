package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.*;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final ImageServiceStorage imageServiceStorage;
    private final ImageService imageService;
    private final ProductCategoryService productCategoryService;
    private final CountryService countryService;
    private final UserService userService;
    private final LotMapper lotMapper;

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
    public LotEntity save(LotEntity lotEntity,  MultipartFile[] files) {
        var userEntity = userService.getById(lotEntity.getUser().getId());
        var countryEntity = countryService.getById(lotEntity.getLocation().getCountry().getId());
        var productCategoryEntity = productCategoryService.getById(lotEntity.getProductCategory().getId());

        lotEntity.setEnabledByAdmin(true);
        lotEntity.setUser(userEntity);
        lotEntity.getLocation().setCountry(countryEntity);
        lotEntity.setProductCategory(productCategoryEntity);

        var saved = lotRepository.save(lotEntity);
        if (files != null)
            saved.setImages(uploadImages(saved.getId(), files));

        return saved;
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
    public LotEntity update(Long id, LotEntity entity,  MultipartFile[] files) {
        clearImagesForLot(id);

        var result = lotMapper.update(getById(id), entity);

        var userEntity = userService.getById(result.getUser().getId());
        var countryEntity = countryService.getById(result.getLocation().getCountry().getId());
        var productCategoryEntity = productCategoryService.getById(result.getProductCategory().getId());

        result.setUser(userEntity);
        result.getLocation().setCountry(countryEntity);
        result.setProductCategory(productCategoryEntity);

        var saved = lotRepository.save(result);

        if (files != null)
            saved.setImages(uploadImages(saved.getId(), files));

        return saved;
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

    public void clearImagesForLot(Long lotId){
        var lotEntity = getById(lotId);
        lotEntity.getImages().forEach(image -> {
            imageService.delete(image);
            imageServiceStorage.remove(image);
        });
    }

}
