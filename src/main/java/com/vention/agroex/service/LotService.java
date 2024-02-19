package com.vention.agroex.service;

import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LotService {
    LotEntity save(LotEntity lotEntity,  MultipartFile[] files);

    LotEntity getById(Long id);

    void deleteById(Long id);

    List<LotEntity> getAll();

    void delete(LotEntity entity);

    LotEntity update(Long id, LotEntity entity, MultipartFile[] files);

    List<ImageEntity>  uploadImages(Long id, MultipartFile[] files);

    void deleteImage(String fileName);

    List<LotEntity> getWithCriteria(Map<String, String> filters);

    void clearImagesForLot(Long lotId);
}
