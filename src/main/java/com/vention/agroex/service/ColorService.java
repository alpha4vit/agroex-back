package com.vention.agroex.service;

import com.vention.agroex.entity.ColorEntity;

import java.util.List;

public interface ColorService {

    ColorEntity getById(Long id);

    List<ColorEntity> getAll();

    ColorEntity save(ColorEntity colorEntity);

    ColorEntity update(Long id, ColorEntity colorEntity);

    ColorEntity getNextColor();
    
}
