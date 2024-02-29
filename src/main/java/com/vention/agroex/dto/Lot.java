package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vention.agroex.entity.TagEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Lot {

    private Long id;

    @NotBlank(message = "Lot title cant be blank")
    @Size(min = 1, max = 30, message = "The field should be from 1 to 30 characters long")
    private String title;

    @NotBlank(message = "Lot description cant be blank")
    @Size(min = 20, max = 1000, message = "The field should be from 20 to 1000 characters long")
    private String description;

    @NotBlank(message = "Lot variety cant be blank")
    @Size(max = 64, message = "The field should be less than 64 characters long")
    private String variety;

    @Size(max = 64, message = "The field should be less than 64 characters long")
    private String size;

    @Size(min = 1, max = 10, message = "The field should be from 1 to 10 characters long")
    private String packaging;

    private Boolean enabledByAdmin;

    private Long duration;

    private float originalMinPrice;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private float minPrice;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String innerStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    @NotNull
    @DecimalMin(value = "0.01", message = "Minimum quantity is 0.01")
    private float quantity;

    @NotNull
    @DecimalMin(value = "1.0", message = "The field should contain only numbers from 1 to 9999 (integer or fractional)")
    private float originalPrice;

    @NotBlank(message = "Lot currency cant be blank")
    @Size(max = 10, message = "Lot currency must be less than 10 characters")
    private String originalCurrency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime creationDate;

    @FutureOrPresent(message = "The field should be valid date greater or equal to current date ")
    private ZonedDateTime expirationDate;

    private ProductCategory productCategory;

    @Pattern(regexp = "\\b(?:buy|sell|auctionSell)\\b", message = "Lot type can be only buy/sell/auctionSell")
    private String lotType;


    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    private Location location;

    private List<TagEntity> tags;

    private List<ImageResponse> images;

    private String adminComment;

    private String currency;

    private float price;

    private List<Bet> bets;
}
