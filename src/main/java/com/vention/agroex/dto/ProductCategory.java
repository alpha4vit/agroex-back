package com.vention.agroex.dto;

import lombok.Data;

@Data
public class ProductCategory {

    private Long id;

    private String title;

    private Long parentId;

}
