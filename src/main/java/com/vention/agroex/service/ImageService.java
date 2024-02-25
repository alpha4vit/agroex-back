package com.vention.agroex.service;

import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    ImageEntity getById(Long id);

    ImageEntity getByName(String name);

    ImageEntity save(ImageEntity imageEntity);

    List<ImageEntity> saveAll(List<ImageEntity> imageEntities);

    void delete(ImageEntity imageEntity);

    void deleteAll(List<ImageEntity> imageEntities);

    List<ImageEntity> getAllByLot(LotEntity lotEntity);

    void updateImagesForLot(LotEntity before, LotEntity result, MultipartFile[] files);

    List<ImageEntity> uploadImages(LotEntity lotEntity, MultipartFile[] files);

}
