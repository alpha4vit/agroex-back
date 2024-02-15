package com.vention.agroex.service;


import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.exception.ImageException;

public interface ImageServiceStorage {

    String uploadToStorage(Image image) throws ImageException;

    void remove(ImageEntity imageEntity);

}
