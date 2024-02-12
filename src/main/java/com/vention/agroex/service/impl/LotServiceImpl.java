package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.*;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.*;
import com.vention.agroex.util.mapper.LotMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final ImageService imageService;
    private final ProductCategoryService productCategoryService;
    private final CountryService countryService;
    private final UserService userService;
    private final LotMapper lotMapper;

    @Override
    public LotEntity save(LotEntity lotEntity) {
        var userEntity = userService.getById(lotEntity.getUser().getId());
        var countryEntity = countryService.getById(lotEntity.getLocation().getCountry().getId());
        var productCategoryEntity = productCategoryService.getById(lotEntity.getProductCategory().getId());

        lotEntity.setEnabledByAdmin(true);
        lotEntity.setUser(userEntity);
        lotEntity.getLocation().setCountry(countryEntity);
        lotEntity.setProductCategory(productCategoryEntity);

        return lotRepository.save(lotEntity);
    }

    @Override
    public LotEntity getById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no such lot with id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        lotRepository.deleteById(id);
    }

    @Override
    public List<LotEntity> getAll() {
        return lotRepository.findAll();
    }

    @Override
    public LotEntity update(Long id, LotEntity entity) {
        var result = lotMapper.update(getById(id), entity);

        var userEntity = userService.getById(result.getUser().getId());
        var countryEntity = countryService.getById(result.getLocation().getCountry().getId());
        var productCategoryEntity = productCategoryService.getById(result.getProductCategory().getId());

        result.setUser(userEntity);
        result.getLocation().setCountry(countryEntity);
        result.setProductCategory(productCategoryEntity);

        return lotRepository.save(result);
    }

    @Override
    public String uploadImage(Long id, Image image) {
        var lotEntity = getById(id);
        return imageService.upload(image, lotEntity);
    }

    @Override
    public void deleteImage(String fileName) {
        var imageEntity = imageService.getByName(fileName);
        imageService.remove(imageEntity);
    }

}
