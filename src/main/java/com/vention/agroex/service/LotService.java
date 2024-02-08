package com.vention.agroex.service;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.LotEntity;

import java.util.List;

public interface LotService {
    LotEntity save(LotEntity lotEntity);

    LotEntity getById(Long id);

    void deleteById(Long id);

    List<LotEntity> getAll();

    LotEntity update(Long id, LotEntity entity);

    String uploadImage(Long id, Image image);

    void deleteImage(String fileName);
}
