package com.vention.agroex.dto;

import com.vention.agroex.entity.ColorEntity;
import lombok.Data;

@Data
public class Tag {

    private Long id;

    private String title;

    private ColorEntity color;

}
