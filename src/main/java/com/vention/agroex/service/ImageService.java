package com.vention.agroex.service;

import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;

import java.util.List;

public interface ImageService {

    ImageEntity getById(Long id);

    ImageEntity getByName(String name);

    ImageEntity save(ImageEntity imageEntity);

    void delete(ImageEntity imageEntity);

    List<ImageEntity> getAllByLot(LotEntity lotEntity);

}
