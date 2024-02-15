package com.vention.agroex.service;

import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LotService {
    LotEntity save(LotEntity lotEntity,  MultipartFile[] files);

    LotEntity getById(Long id);

    void deleteById(Long id);

    List<LotEntity> getAll();

    void delete(LotEntity entity);

    LotEntity update(Long id, LotEntity entity, MultipartFile[] files);

    List<ImageEntity>  uploadImages(Long id, MultipartFile[] files);

    void deleteImage(String fileName);

    void clearImagesForLot(Long lotId);
}
