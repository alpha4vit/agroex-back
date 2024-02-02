package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.LotDTO;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.service.ProductCategoryService;
import com.vention.agroex.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class LotMapper {

    @Autowired
    protected UserService userService;
    @Autowired
    protected ProductCategoryService productCategoryService;

    @Mapping(target = "user", expression = "java(userService.getById(lotDTO.getUserId()))")
    @Mapping(target = "productCategory", expression = "java(productCategoryService.getById(lotDTO.getProductCategoryId()))")
    @Mapping(target = "creationDate", ignore = true)
    public abstract Lot toEntity(LotDTO lotDTO);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productCategoryId", source = "productCategory.id")
    public abstract LotDTO toDTO(Lot lot);

    @Mapping(target = "lotType", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "user", expression = "java(userService.getById(lotDTO.getUserId()))")
    @Mapping(target = "productCategory", expression = "java(productCategoryService.getById(lotDTO.getProductCategoryId()))")
    public abstract void update(@MappingTarget Lot lot, LotDTO lotDTO);

    public abstract List<Lot> toEntities(List<LotDTO> dtos);

    public abstract List<LotDTO> toDTOs(List<Lot> lots);

}
