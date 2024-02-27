package com.vention.agroex.service.impl;


import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.repository.ImageRepository;
import com.vention.agroex.service.ImageService;
import com.vention.agroex.service.ImageServiceStorage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageServiceStorage imageServiceStorage;

    @Override
    public ImageEntity getById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image with this id not found!"));
    }

    @Override
    public ImageEntity getByName(String name) {
        return imageRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Image with this name not found!"));
    }

    @Override
    public ImageEntity save(ImageEntity imageEntity) {
        return imageRepository.save(imageEntity);
    }

    @Override
    public List<ImageEntity> saveAll(List<ImageEntity> imageEntities) {
        return imageRepository.saveAll(imageEntities);
    }

    @Override
    public void delete(ImageEntity imageEntity) {
        imageRepository.delete(imageEntity);
    }

    @Override
    public void deleteAll(List<ImageEntity> imageEntities) {
        imageRepository.deleteAll(imageEntities);

    }

    @Override
    public List<ImageEntity> getAllByLot(LotEntity lotEntity) {
        return imageRepository.findByLot(lotEntity);
    }

    @Override
    public void updateImagesForLot(LotEntity before, LotEntity result, MultipartFile[] files) {
        if (validateImages(result, files)) {
            if (files != null)
                result.getImages().addAll(uploadImages(before, files));
        }
        else
            throw new ImageLotException("Invalid arguments!", Map.of("images", "Incorrect quantity of images must be from 1 to 6!"));

        var imagesForDelete = before.getImages().stream()
                .filter(image -> !result.getImages().contains(image))
                .toList();
        deleteAll(imagesForDelete);
        imageServiceStorage.removeAll(imagesForDelete);
    }

    private boolean validateImages(LotEntity result, MultipartFile[] files){
        return files != null &&
                files.length + result.getImages().size() >= 1 &&
                !(files.length + result.getImages().size() > 6) || !result.getImages().isEmpty() && result.getImages().size() <= 6;
    }

    @Override
    public List<ImageEntity> uploadImages(LotEntity lotEntity, MultipartFile[] files) {
        List<ImageEntity> images = new ArrayList<>();
        try {
            Arrays.stream(files).forEach(file -> {
                String imageName = imageServiceStorage.uploadToStorage(new Image(file));
                images.add(ImageEntity.builder()
                        .lot(lotEntity)
                        .name(imageName)
                        .build());
            });
            return saveAll(images);
        } catch (ImageException e) {
            images.forEach(imageServiceStorage::remove);
            throw new ImageLotException(e.getMessage(), Map.of("images", e.getMessage()));
        }
    }
}
