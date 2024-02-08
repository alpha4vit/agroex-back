package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = LocationMapper.class)
public interface LotMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "user.id", source = "lot.userId")
    @Mapping(target = "productCategory.id", source = "lot.productCategoryId")
    LotEntity toEntity(Lot lot);

    @Mapping(target = "userId", source = "lotEntity.user.id")
    @Mapping(target = "productCategoryId", source = "lotEntity.productCategory.id")
    Lot toDTO(LotEntity lotEntity);

    List<LotEntity> toEntities(List<Lot> lots);

    List<Lot> toDTOs(List<LotEntity> lotEntities);

    default LotEntity update(LotEntity before, LotEntity received) {
        LotEntity result = LotEntity.builder()
                .id(before.getId())
                .title(received.getTitle() != null ? received.getTitle() : before.getTitle())
                .description(received.getDescription() != null ? received.getDescription() : before.getDescription())
                .currency(received.getCurrency() != null ? received.getCurrency() : before.getCurrency())
                .lotType(before.getLotType())
                .enabledByAdmin(before.getEnabledByAdmin())
                .creationDate(before.getCreationDate())
                .expirationDate(received.getExpirationDate() != null ? received.getExpirationDate() : before.getExpirationDate())
                .pricePerTon(received.getPricePerTon() != 0.0 ? received.getPricePerTon() : before.getPricePerTon())
                .size(received.getSize() != null ? received.getSize() : before.getSize())
                .quantity(received.getQuantity() != 0 ? received.getQuantity() : before.getQuantity())
                .packaging(received.getPackaging() != null ? received.getPackaging() : before.getPackaging())
                .variety(received.getVariety() != null ? received.getVariety() : before.getVariety())
                .images(before.getImages())
                .tags(received.getTags())
                .enabledByAdmin(received.getEnabledByAdmin())
                .build();

        LocationEntity locationEntity = LocationEntity.builder()
                .id(received.getLocation().getId())
                .country(
                        CountryEntity.builder()
                                .id(received.getLocation().getCountry().getId())
                                .build()
                )
                .latitude(received.getLocation().getLatitude())
                .longitude(received.getLocation().getLongitude())
                .region(received.getLocation().getRegion())
                .build();
        result.setLocation(locationEntity);

        result.setProductCategory(ProductCategoryEntity.builder()
                .id(received.getProductCategory().getId())
                .build());
        result.setUser(UserEntity.builder()
                .id(received.getUser().getId())
                .build());

        return result;
    }


}