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

    @NotNull
    @DecimalMin(value = "0.01", message = "Minimum quantity is 0.01")
    private float quantity;

    @NotNull
    @DecimalMin(value = "1.0", message = "The field should contain only numbers from 1 to 9999 (integer or fractional)")
    private float price;

    @NotBlank(message = "Lot currency cant be blank")
    @Size(max = 10, message = "Lot currency must be less than 10 characters")
    private String currency;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant creationDate;

    @FutureOrPresent(message = "The field should be valid date greater or equal to current date ")
    private Instant expirationDate;

    private ProductCategoryModel productCategory;

    @Pattern(regexp = "\\b(?:buy|sell)\\b", message = "Lot type can be only buy/sell")
    private String lotType;

    private Long userId;

    private Location location;

    private List<TagEntity> tags;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<ImageResponse> images;
}
