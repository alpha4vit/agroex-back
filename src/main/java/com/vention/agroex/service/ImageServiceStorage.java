package com.vention.agroex.service;


import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.exception.ImageException;

import java.util.List;

public interface ImageServiceStorage {

    String uploadToStorage(Image image) throws ImageException;

    void remove(ImageEntity imageEntity);

    void removeAll(List<ImageEntity> imageEntities);

}
