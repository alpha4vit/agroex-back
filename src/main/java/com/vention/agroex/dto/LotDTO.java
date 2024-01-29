package com.vention.agroex.dto;

import com.vention.agroex.entity.Image;
import com.vention.agroex.entity.Location;
import com.vention.agroex.entity.Tag;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class LotDTO {

    private Long id;

    private String title;

    private String description;

    private String variety;

    private String size;

    private String packaging;

    private Boolean enabledByAdmin;

    private int quantity;

    private float pricePerTon;

    private String currency;

    private Instant creationDate;

    private Instant expirationDate;

    private Long productCategoryId;

    private String lotType;

    private Long userId;

    private LocationDTO location;

    private List<Tag> tags;

    private List<Image> images;
}
