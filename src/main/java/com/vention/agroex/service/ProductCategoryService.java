package com.vention.agroex.service;

import com.vention.agroex.entity.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductCategoryService {
    ProductCategory save(ProductCategory productCategory);

    ProductCategory getById(Long id);

    List<ProductCategory> getSubcategoriesById(Long id);

    void deleteById(Long id);

    List<ProductCategory> getAll();

    ProductCategory update(Long id, ProductCategory productCategory);
}
