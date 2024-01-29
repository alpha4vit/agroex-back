package com.vention.agroex.entity;

import lombok.Data;


@Data
public class Location {

    private Long id;

    private String country;

    private String region;

    private String latitude;

    private String longitude;
}
