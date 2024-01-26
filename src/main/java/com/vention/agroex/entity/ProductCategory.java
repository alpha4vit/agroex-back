package com.vention.agroex.entity;

import lombok.Data;


@Data
public class ProductCategory {

    private Long id;

    private String title;

    private Long parentId;
}
