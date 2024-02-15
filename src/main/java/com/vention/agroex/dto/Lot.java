package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vention.agroex.entity.TagEntity;
import com.vention.agroex.model.ProductCategoryModel;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Lot {

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

    private Boolean enabledByAdmin;

    @NotNull
    @DecimalMin(value = "0.01", message = "Minimum quantity is 0.01")
    private float quantity;

    @NotNull
    private float price;

    @NotBlank(message = "Lot currency cant be blank")
    @Size(max = 10, message = "Lot currency must be less than 10 characters")
    private String currency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant creationDate;

    private Instant expirationDate;

    private ProductCategoryModel productCategory;

    @Pattern(regexp = "\\b(?:buy|sell)\\b", message = "Lot type can be only buy/sell")
    private String lotType;

    private Long userId;

    private Location location;

    private List<TagEntity> tags;

    private List<ImageResponse> images;
}
