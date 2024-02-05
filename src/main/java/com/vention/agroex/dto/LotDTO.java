package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vention.agroex.entity.Image;
import com.vention.agroex.entity.Tag;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class LotDTO {

    private Long id;

    @NotBlank(message = "Lot title cant be blank")
    @Size(min = 5, max = 64, message = "Lot title must be between 5 and 64 characters")
    private String title;

    @NotBlank(message = "Lot description cant be blank")
    private String description;

    @NotBlank(message = "Lot variety cant be blank")
    @Size(max = 64, message = "Lot variety must be less than 64 characters")
    private String variety;

    @Size(max = 64, message = "Lot size must be less than 64 characters")
    private String size;

    @Size(max = 64, message = "Lot packaging must be less than 64 characters")
    private String packaging;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabledByAdmin;

    @NotNull
    @Min(value = 1, message = "Minimum quantity is 1")
    private int quantity;

    @NotNull
    private float pricePerTon;

    @NotBlank(message = "Lot currency cant be blank")
    @Size(max = 10, message = "Lot currency must be less than 10 characters")
    private String currency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant creationDate;

    private Instant expirationDate;

    private Long productCategoryId;

    @Pattern(regexp = "\\b(?:buy|sell)\\b", message = "Lot type can be only buy/sell")
    private String lotType;

    private Long userId;

    private LocationDTO location;

    private List<Tag> tags;

    private List<Image> images;
}
