package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Image;
import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.repository.ProductCategoryRepository;
import com.vention.agroex.service.ImageServiceStorage;
import com.vention.agroex.service.ProductCategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ImageServiceStorage imageServiceStorage;

    @Override
    public ProductCategoryEntity save(ProductCategoryEntity productCategoryEntity, MultipartFile file) {
        if (file != null) {
            productCategoryEntity.setImage(
                    imageServiceStorage.uploadToStorage(
                            new Image(file)
                    )
            );
        }
        if (productCategoryEntity.getParent() == null) {
            productCategoryEntity.setParent(
                    productCategoryRepository.getRootCategory()
            );
        }
        productCategoryEntity.setTitle(StringUtils.normalizeSpace(productCategoryEntity.getTitle()));
        return productCategoryRepository.save(productCategoryEntity);
    }

    @Override
    public ProductCategoryEntity getById(Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("There is no product category with id %d", id)));
    }

    @Override
    public ProductCategoryEntity getByTitle(String title) {
        return productCategoryRepository.getByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no product category with title %s", title)));
    }

    @Override
    public boolean existsByTitle(String title) {
        return productCategoryRepository.existsByTitle(title);
    }

    @Override
    public List<ProductCategoryEntity> getSubcategoriesById(Long parentId, Boolean lotExisted) {
        var categories = lotExisted ? productCategoryRepository.findProductCategoryListByParentIdAndLotsIsNotEmpty(parentId) :
                productCategoryRepository.findProductCategoryListByParentId(parentId);
        return categories.orElseThrow(
                () -> new EntityNotFoundException(String.format("There is no product category with parentId %d", parentId)));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productCategoryRepository.deleteById(id);
        productCategoryRepository.deleteByParentId(id);
    }

    @Override
    public List<ProductCategoryEntity> getAll() {
        return productCategoryRepository.findAll();
    }

    @Override
    public ProductCategoryEntity update(Long id, ProductCategoryEntity productCategoryEntity, MultipartFile file) {
        var fetchedCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no category with id %d", id)));
        productCategoryEntity.setId(id);
        productCategoryEntity.setParent(fetchedCategory.getParent());
        productCategoryEntity.setImage(fetchedCategory.getImage());
        if (file != null){
            productCategoryEntity.setImage(
                    imageServiceStorage.uploadToStorage(
                            new Image(file)
                    )
            );
        }
        return productCategoryRepository.save(productCategoryEntity);
    }

    @Override
    public List<ProductCategoryEntity> getAllSubCategories(Long productCategoryId) {
        var categories = getAll();
        return categories.stream()
                .filter(category -> searchParent(category, productCategoryId))
                .toList();
    }

    private boolean searchParent(ProductCategoryEntity entity, Long searchId){
        if (entity.getId().equals(searchId))
            return true;
        if (entity.getParent() == null)
            return false;
        if (entity.getParent().getId().equals(searchId))
            return true;
        return searchParent(entity.getParent(), searchId);
    }

}
