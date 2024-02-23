package com.vention.agroex.service;

import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.model.LotStatusResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface LotService {
    LotEntity save(LotEntity lotEntity,  MultipartFile[] files);
    
    LotEntity save(LotEntity lotEntity, MultipartFile[] files, String currency);

    LotEntity getById(Long id);
    LotEntity getById(Long id, String currency);

    void deleteById(Long id);

    List<LotEntity> getAll();
    List<LotEntity> getAll(String currency);
    
    void delete(LotEntity entity);

    LotEntity update(Long id, LotEntity entity, MultipartFile[] files);
    LotEntity update(Long id, LotEntity entity, MultipartFile[] files, String currency);

    LotEntity update(Long id, LotEntity entity);

    List<ImageEntity>  uploadImages(Long id, MultipartFile[] files);

    void deleteImage(String fileName);

    List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize);
    List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize, String currency);

    void clearImagesForLot(Long lotId);

    LotStatusResponse getLotStatus(Long id);

    LotEntity putOnModeration(Long lotId);
    LotEntity putOnModeration(Long lotId, String currency);

    LotEntity approve(Long lotId);
    LotEntity approve(Long lotId, String currency);
}
