package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.entity.*;
import com.vention.agroex.model.LotReportModel;
import com.vention.agroex.util.constant.StatusConstants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {LocationMapper.class, ProductCategoryMapper.class, BetMapper.class},
        imports = {ZonedDateTime.class, ZoneId.class})
public interface LotMapper {

    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "enabledByAdmin", defaultValue = "true")
    @Mapping(target = "productCategory", qualifiedByName = "toProductCategoryEntity")
    @Mapping(target = "adminComment", defaultValue = "")
    @Mapping(target = "userStatus", defaultValue = StatusConstants.ACTIVE)
    @Mapping(target = "status", defaultValue = StatusConstants.ACTIVE)
    @Mapping(target = "innerStatus", defaultValue = StatusConstants.APPROVED)
    LotEntity toEntity(Lot lot);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "productCategory", qualifiedByName = "toProductCategoryDTO")
    Lot toDTO(LotEntity lotEntity);

    List<LotEntity> toEntities(List<Lot> lots);

    List<Lot> toDTOs(List<LotEntity> lotEntities);

    List<Lot> toDTOsFromLotModels(List<LotReportModel> lotReportModels);

    default LotEntity update(LotEntity before, LotEntity received) {
        LotEntity result = LotEntity.builder()
                .id(before.getId())
                .title(received.getTitle() != null ? received.getTitle() : before.getTitle())
                .description(received.getDescription() != null ? received.getDescription() : before.getDescription())
                .lotType(before.getLotType())
                .originalCurrency(received.getOriginalCurrency() != null ? received.getOriginalCurrency() : before.getOriginalCurrency())
                .currency(received.getCurrency() != null ? received.getCurrency() : before.getCurrency())
                .currencyRates(before.getCurrencyRates())
                .enabledByAdmin(before.getEnabledByAdmin())
                .creationDate(before.getCreationDate())
                .expirationDate(received.getExpirationDate() != null ? received.getExpirationDate() : before.getExpirationDate())
                .actualStartDate(received.getActualStartDate() != null ? received.getActualStartDate() : before.getActualStartDate())
                .originalPrice(received.getOriginalPrice().compareTo(BigDecimal.valueOf(0.0)) != 0 ? received.getOriginalPrice() : before.getOriginalPrice())
                .originalMinPrice(received.getOriginalMinPrice() != null ? received.getOriginalMinPrice() : before.getOriginalMinPrice())
                .size(received.getSize() != null ? received.getSize() : before.getSize())
                .quantity(received.getQuantity() != 0 ? received.getQuantity() : before.getQuantity())
                .packaging(received.getPackaging() != null ? received.getPackaging() : before.getPackaging())
                .variety(received.getVariety() != null ? received.getVariety() : before.getVariety())
                .images(received.getImages())
                .tags(received.getTags())
                .enabledByAdmin(received.getEnabledByAdmin())
                .bets(received.getBets())
                .status(received.getStatus())
                .userStatus((received.getUserStatus()))
                .innerStatus((received.getInnerStatus()))
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