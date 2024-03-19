package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "product_category")
public class ProductCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ProductCategoryEntity parent;

    @ToString.Exclude
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.REMOVE)
    private List<LotEntity> lots;
}
