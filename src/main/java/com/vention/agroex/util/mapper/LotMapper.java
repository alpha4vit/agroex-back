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
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "productCategory.id", source = "productCategory.id")
    @Mapping(target = "enabledByAdmin", defaultValue = "true")
    @Mapping(target = "adminComment", defaultValue = "")
    LotEntity toEntity(Lot lot);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productCategory.id", source = "productCategory.id")
    Lot toDTO(LotEntity lotEntity);

    List<LotEntity> toEntities(List<Lot> lots);

    List<Lot> toDTOs(List<LotEntity> lotEntities);

    default LotEntity update(LotEntity before, LotEntity received) {
        LotEntity result = LotEntity.builder()
                .id(before.getId())
                .title(received.getTitle() != null ? received.getTitle() : before.getTitle())
                .description(received.getDescription() != null ? received.getDescription() : before.getDescription())
                .originalCurrency(received.getOriginalCurrency() != null ? received.getOriginalCurrency() : before.getOriginalCurrency())
                .lotType(before.getLotType())
                .enabledByAdmin(before.getEnabledByAdmin())
                .creationDate(before.getCreationDate())
                .expirationDate(received.getExpirationDate() != null ? received.getExpirationDate() : before.getExpirationDate())
                .originalPrice(received.getOriginalPrice() != 0.0 ? received.getOriginalPrice() : before.getOriginalPrice())
                .size(received.getSize() != null ? received.getSize() : before.getSize())
                .quantity(received.getQuantity() != 0 ? received.getQuantity() : before.getQuantity())
                .packaging(received.getPackaging() != null ? received.getPackaging() : before.getPackaging())
                .variety(received.getVariety() != null ? received.getVariety() : before.getVariety())
                .images(received.getImages())
                .tags(received.getTags())
                .enabledByAdmin(received.getEnabledByAdmin())
                .bets(received.getBets())
                .status(before.getStatus())
                .userStatus((before.getUserStatus()))
                .innerStatus((before.getInnerStatus()))
                .duration(received.getDuration())
                .adminComment(received.getAdminComment())
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