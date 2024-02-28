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

    ProductCategoryEntity toEntity(ProductCategory category);

    @Named("toDTO")
    @Mapping(target = "parentId", source = "entity.parent.id")
    ProductCategory toDTO(ProductCategoryEntity entity);

    @IterableMapping(qualifiedByName = "toDTO")
    List<ProductCategory> toDTOs(List<ProductCategoryEntity> entities);

    List<ProductCategoryEntity> toEntities(List<ProductCategory> categories);
}
