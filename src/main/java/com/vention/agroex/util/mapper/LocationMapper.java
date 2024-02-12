package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Location;
import com.vention.agroex.entity.LocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    @Mapping(target = "country.id", source = "locationDTO.countryId")
    LocationEntity toEntity(Location locationDTO);

    @Mapping(target = "countryId", source = "location.country.id")
    @Mapping(target = "countryName", source = "location.country.name")
    Location toDTO(LocationEntity location);
    List<LocationEntity> toEntities(List<Location> dtos);

    List<Location> toDTOs(List<LocationEntity> lots);


}