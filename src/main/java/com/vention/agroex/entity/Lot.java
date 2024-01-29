package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Entity(name = "lot")
@NoArgsConstructor
@AllArgsConstructor
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "variety")
    private String variety;

    @Column(name = "size")
    private String size;

    @Column(name = "packaging")
    private String packaging;

    @Column(name = "enabled_by_admin")
    private Boolean enabledByAdmin;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price_per_ton")
    private float pricePerTon;

    @Column(name = "currency")
    private String currency;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "expiration_date")
    private Instant expirationDate;

    @ManyToOne
    private ProductCategory productCategory;

    @Column(name = "lot_type")
    private String lotType;

    @ManyToOne
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @ElementCollection
    private List<Tag> tags;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Image> images;

}