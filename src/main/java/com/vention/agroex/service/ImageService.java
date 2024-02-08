package com.vention.agroex.service;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;

public interface ImageService {

    ImageEntity getById(Long id);

    ImageEntity getByName(String name);

    String upload(Image image, LotEntity lotEntity);

    void remove(ImageEntity imageEntity);

}
