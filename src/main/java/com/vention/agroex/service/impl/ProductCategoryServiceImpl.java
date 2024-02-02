package com.vention.agroex.service.impl;

import com.vention.agroex.entity.ProductCategory;
import com.vention.agroex.repository.ProductCategoryRepository;
import com.vention.agroex.service.ProductCategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductCategory save(ProductCategory productCategory) {
        if (productCategory.getParentId() == null) {
            productCategory.setParentId(0L);
        }
        return productCategoryRepository.save(productCategory);
    }

    @Override
    public ProductCategory getById(Long id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("There is no lots with id %d", id)));
    }

    @Override
    public List<ProductCategory> getSubcategoriesById(Long parentId) {
        return productCategoryRepository.findProductCategoryListByParentId(parentId);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        productCategoryRepository.deleteById(id);
        productCategoryRepository.deleteByParentId(id);
    }

    @Override
    public List<ProductCategory> getAll() {
        return productCategoryRepository.findAll();
    }

    @Override
    public ProductCategory update(Long id, ProductCategory productCategory) {
        if (productCategoryRepository.existsById(id)) {
            return productCategoryRepository.save(productCategory);
        } else {
            throw new EntityNotFoundException(String.format("There is no category with id %d", id));
        }
    }
}
