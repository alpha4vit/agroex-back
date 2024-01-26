package com.vention.agroex.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class Location {

    private Long id;

    private String country;

    private String region;

    private String latitude;

    private String longitude;
}
