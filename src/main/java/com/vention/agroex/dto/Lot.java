package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vention.agroex.entity.TagEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
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

    @NotNull(message = "Please fill in the field")
    @Size(min = 1, max = 30, message = "The field should be from 1 to 30 characters long")
    private String title;

    @NotNull(message = "Please fill in the field")
    @Size(min = 20, max = 1000, message = "The field should be from 20 to 1000 characters long")
    private String description;

    @NotNull(message = "Please fill in the field")
    @Size(max = 64, message = "The field should be less than 64 characters long")
    private String variety;

    @NotNull(message = "Please fill in the field")
    @Size(min = 1, max = 10, message = "The field should be from 1 to 10 characters long")
    private String size;

    @NotNull(message = "Please fill in the field")
    @Size(min = 1, max = 10, message = "The field should be from 1 to 10 characters long")
    private String packaging;

    private Boolean enabledByAdmin;

    private Long duration;

    @NotNull(message = "Please fill in the field")
    @DecimalMin(value = "1.0", message = "The field should contain only numbers from 1 to 9999 (integer or fractional)")
    private BigDecimal originalPrice;

    private BigDecimal originalMinPrice;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String innerStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    @NotNull(message = "Please fill in the field")
    @DecimalMin(value = "1.00", message = "The field should contain only numbers from 1 to 999 (integer or fractional)")
    private float quantity;

    @NotNull(message = "Please fill in the field")
    @Size(max = 10, message = "Lot currency must be less than 10 characters")
    private String originalCurrency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime creationDate;

    @FutureOrPresent(message = "The field should be valid date greater or equal to current date")
    private ZonedDateTime expirationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ZonedDateTime actualStartDate;

    @Valid
    private ProductCategory productCategory;

    @NotNull(message = "Please fill in the field")
    @Pattern(regexp = "\\b(?:buy|sell|auctionSell)\\b", message = "Lot type can be only buy/sell/auctionSell")
    private String lotType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;

    @Valid
    private Location location;

    private List<TagEntity> tags;

    private List<ImageResponse> images;

    private String adminComment;

    private String currency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal price;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal minPrice;

    private List<Bet> bets;
}
