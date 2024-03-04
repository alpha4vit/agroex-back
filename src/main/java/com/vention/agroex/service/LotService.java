package com.vention.agroex.service;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.model.LotStatusResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LotService {
    LotEntity save(LotEntity lotEntity,  MultipartFile[] files);

    LotEntity save(LotEntity lotEntity, MultipartFile[] files, String currency);

    LotEntity getById(Long id);
    LotEntity getById(Long id, String currency);

    void deleteById(Long id);

    List<LotEntity> getAll();

    List<LotEntity> getAll(String currency);

    void delete(LotEntity entity);


    LotEntity update(Long id, LotEntity entity, MultipartFile[] files, String currency);

    LotEntity update(Long id, LotEntity entity);

    void deleteImage(String fileName);

    List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize, String currency);

    LotStatusResponse getLotStatus(Long id);

    LotEntity putOnModeration(Long lotId, String currency, String adminComment);

    LotEntity approve(Long id, String currency, String adminComment);

    LotEntity reject(Long lotId, String adminComment);

    LotEntity changeUserStatus(Long id, boolean status);

    void finishAuction(LotEntity lot);

    LotEntity makeDeal(Long lotId, UUID userId);
}
