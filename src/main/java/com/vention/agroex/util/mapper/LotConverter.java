package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.LocationDTO;
import com.vention.agroex.dto.LotDTO;
import com.vention.agroex.entity.Location;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.service.CountryService;
import com.vention.agroex.service.ProductCategoryService;
import com.vention.agroex.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Data
@Component
@RequiredArgsConstructor
public class LotConverter {

    private final ProductCategoryService productCategoryService;
    private final UserService userService;
    private final CountryService countryService;

    public Lot toEntity(LotDTO lotDTO) {
        Lot lot = Lot.builder()
                .id(lotDTO.getId())
                .title(lotDTO.getTitle())
                .description(lotDTO.getDescription())
                .variety(lotDTO.getVariety())
                .size(lotDTO.getSize())
                .packaging(lotDTO.getPackaging())
                .enabledByAdmin(lotDTO.getEnabledByAdmin())
                .quantity(lotDTO.getQuantity())
                .pricePerTon(lotDTO.getPricePerTon())
                .currency(lotDTO.getCurrency())
                .expirationDate(lotDTO.getExpirationDate())
                .lotType(lotDTO.getLotType())
                .tags(lotDTO.getTags())
                .build();

        Optional.of(lotDTO.getProductCategoryId())
                .ifPresent(id -> {
                    var productCategory = productCategoryService.getById(id);
                    lot.setProductCategory(productCategory);
                });

        Optional.of(lotDTO.getUserId())
                .ifPresent(id -> {
                    var user = userService.getById(id);
                    lot.setUser(user);
                });

        Optional.of(lotDTO.getLocation().getCountryId())
                .ifPresent(id -> {
                    var country = countryService.getById(id);
                    lot.setLocation(Location.builder()
                                    .country(country)
                                    .longitude(lotDTO.getLocation().getLongitude())
                                    .latitude(lotDTO.getLocation().getLatitude())
                                    .region(lotDTO.getLocation().getRegion())
                            .build());
                });

        return lot;
    }

    public void updateFields(Lot lot, LotDTO lotDTO) {
        lot.setTitle(lotDTO.getTitle());
        lot.setDescription(lotDTO.getDescription());
        lot.setVariety(lotDTO.getVariety());
        lot.setSize(lotDTO.getSize());
        lot.setPackaging(lotDTO.getPackaging());
        lot.setEnabledByAdmin(lotDTO.getEnabledByAdmin());
        lot.setQuantity(lotDTO.getQuantity());
        lot.setPricePerTon(lotDTO.getPricePerTon());
        lot.setCurrency(lotDTO.getCurrency());
        lot.setCreationDate(lotDTO.getCreationDate());
        lot.setExpirationDate(lotDTO.getExpirationDate());
        lot.setTags(lotDTO.getTags());

        Optional.of(lotDTO.getProductCategoryId())
                .ifPresent(id -> lot.setProductCategory(productCategoryService
                        .getById(id)));

        Optional.of(lotDTO.getUserId())
                .ifPresent(id -> lot.setUser(userService
                        .getById(id)));

        Optional.of(lotDTO.getLocation().getCountryId())
                .ifPresent(id -> {
                    var country = countryService.getById(id);
                    lot.setLocation(Location.builder()
                            .country(country)
                            .longitude(lotDTO.getLocation().getLongitude())
                            .latitude(lotDTO.getLocation().getLatitude())
                            .region(lotDTO.getLocation().getRegion())
                            .build());
                });
    }

    public LotDTO toDTO(Lot lot) {
        LotDTO lotDTO = LotDTO.builder()
                .id(lot.getId())
                .title(lot.getTitle())
                .description(lot.getDescription())
                .variety(lot.getVariety())
                .size(lot.getSize())
                .packaging(lot.getPackaging())
                .enabledByAdmin(lot.getEnabledByAdmin())
                .quantity(lot.getQuantity())
                .pricePerTon(lot.getPricePerTon())
                .currency(lot.getCurrency())
                .creationDate(lot.getCreationDate())
                .expirationDate(lot.getExpirationDate())
                .lotType(lot.getLotType())
                .tags(lot.getTags())
                .build();

        Optional.ofNullable(lot.getProductCategory())
                .ifPresent(productCategory -> lotDTO.setProductCategoryId(productCategory.getId()));

        Optional.ofNullable(lot.getUser())
                .ifPresent(user -> lotDTO.setUserId(user.getId()));

        Optional.of(lot.getLocation().getCountry())
                .ifPresent(country -> {
                    lotDTO.setLocation(LocationDTO.builder()
                            .countryId(country.getId())
                            .longitude(lot.getLocation().getLongitude())
                            .latitude(lot.getLocation().getLatitude())
                            .region(lot.getLocation().getRegion())
                            .build());
                });

        return lotDTO;
    }
}