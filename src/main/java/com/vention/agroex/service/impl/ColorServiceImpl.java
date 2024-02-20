package com.vention.agroex.service.impl;

import com.vention.agroex.entity.ColorEntity;
import com.vention.agroex.repository.ColorRepository;
import com.vention.agroex.service.ColorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;


    @Override
    public ColorEntity getById(Long id) {
        return colorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Color with this id not found!"));
    }

    @Override
    public List<ColorEntity> getAll() {
        return colorRepository.findAll();
    }

    @Override
    public ColorEntity save(ColorEntity colorEntity) {
        return colorRepository.save(colorEntity);
    }

    @Override
    public ColorEntity update(Long id, ColorEntity colorEntity) {
        colorEntity.setId(id);
        return colorRepository.save(colorEntity);
    }

    @Override
    public ColorEntity getNextColor() {
        return colorRepository.findNextColor();
    }
}
