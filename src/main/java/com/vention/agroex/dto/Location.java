package com.vention.agroex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull(message = "Please fill the field")
    private Long countryId;

    private String countryName;

    @NotNull(message = "Please fill in the field")
    @Size(max = 64, message = "Region must be less than 64 characters")
    private String region;

    private String latitude;
    private String longitude;
}
