package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.ProductCategory;
import com.vention.agroex.entity.ProductCategoryEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    @Named("toProductCategoryEntity")
    @Mapping(target = "parent.id", source = "parentId")
    ProductCategoryEntity toEntity(ProductCategory category);

    @Named("toProductCategoryDTO")
    @Mapping(target = "parentId", source = "entity.parent.id")
    ProductCategory toDTO(ProductCategoryEntity entity);

    @IterableMapping(qualifiedByName = "toProductCategoryDTO")
    List<ProductCategory> toDTOs(List<ProductCategoryEntity> entities);

    @IterableMapping(qualifiedByName = "toProductCategoryEntity")
    List<ProductCategoryEntity> toEntities(List<ProductCategory> categories);
}
