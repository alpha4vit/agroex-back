package com.vention.agroex.entity;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class Lot {

    private Long id;

    private String title;

    private String description;

    private int quantity;

    private float pricePerTon;

    private String currency;

    private Instant creationDate;

    private Instant expirationDate;

    private ProductCategory productCategory;

    private String lotType;

    private User user;

    private Location location;

    private List<Tag> tags;

    private List<Image> images;

}