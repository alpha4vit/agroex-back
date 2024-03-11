package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Country;
import com.vention.agroex.entity.CountryEntity;
import com.vention.agroex.entity.LocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", imports = LocationEntity.class)
public interface CountryMapper {

    CountryEntity toEntity(Country country);

    @Mapping(target = "regions", expression = "java(countryEntity.getLocations().stream().map(LocationEntity::getRegion).toList())")
    Country toDTO(CountryEntity countryEntity);

    List<CountryEntity> toEntities(List<Country> dtos);

    List<Country> toDtos(List<CountryEntity> users);
}
