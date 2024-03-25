package com.vention.agroex.dto;

import com.vention.agroex.entity.ColorEntity;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Tag {

    private Long id;

    @Size(min = 1, max = 10, message = "The field should be from 1 to 10 characters long")
    private String title;

    private ColorEntity color;

}
