package com.vention.agroex.service;

import com.vention.agroex.dto.ImageDTO;
import com.vention.agroex.entity.Image;
import com.vention.agroex.entity.Lot;

public interface ImageService {

    Image getById(Long id);

    Image getByName(String name);

    String upload(final ImageDTO image, Lot lot);

    void remove(Image image);

}
