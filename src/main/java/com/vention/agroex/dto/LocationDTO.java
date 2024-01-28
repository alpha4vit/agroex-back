package com.vention.agroex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private Long countryId;

    @NotBlank(message = "Region can`t be blank")
    @Size(max = 64, message = "Region must be less than 64 characters")
    private String region;

    private String latitude;
    private String longitude;
}
