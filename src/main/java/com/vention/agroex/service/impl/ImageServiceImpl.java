package com.vention.agroex.service.impl;


import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.repository.ImageRepository;
import com.vention.agroex.service.ImageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public ImageEntity getById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image with this id not found!"));
    }

    @Override
    public ImageEntity getByName(String name) {
        return imageRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Image with this name not found!"));
    }

    @Override
    public ImageEntity save(ImageEntity imageEntity) {
        return imageRepository.save(imageEntity);
    }

    @Override
    public void delete(ImageEntity imageEntity) {
        imageRepository.delete(imageEntity);
    }

    @Override
    public List<ImageEntity> getAllByLot(LotEntity lotEntity) {
        return imageRepository.findByLot(lotEntity);
    }

}
