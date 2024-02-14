package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.ProductCategory;
import com.vention.agroex.entity.ProductCategoryEntity;

import java.util.List;

public interface ProductCategoryMapper {

    ProductCategoryEntity toEntity(ProductCategory category);

    ProductCategory toDTO(ProductCategoryEntity entity);

    List<ProductCategory> toDTOs(List<ProductCategoryEntity> entities);

    List<ProductCategoryEntity> toEntities(List<ProductCategory> categories);
}
