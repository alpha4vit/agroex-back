package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.LocationDTO;
import com.vention.agroex.service.CountryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class LocationMapper {
    @Autowired
    protected CountryService countryService;

    @Mapping(target = "countryName", expression = "java(countryService.getById(locationDTO.getCountryId()).getName())")
    public abstract LocationDTO toResponse(LocationDTO locationDTO);
}
