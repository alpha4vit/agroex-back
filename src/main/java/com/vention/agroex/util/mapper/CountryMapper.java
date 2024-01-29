package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.CountryDTO;
import com.vention.agroex.entity.Country;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    Country toEntity(CountryDTO countryDTO);

    CountryDTO toDTO(Country country);

    List<Country> toEntities(List<CountryDTO> dtos);

    List<CountryDTO> toDtos(List<Country> users);
}
