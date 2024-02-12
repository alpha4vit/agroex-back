package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Country;
import com.vention.agroex.entity.CountryEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryEntity toEntity(Country country);

    Country toDTO(CountryEntity countryEntity);

    List<CountryEntity> toEntities(List<Country> dtos);

    List<Country> toDtos(List<CountryEntity> users);
}
