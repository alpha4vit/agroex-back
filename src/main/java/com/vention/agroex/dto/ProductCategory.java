package com.vention.agroex.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCategory(
        Long id,

        @Size(min = 1, max = 30, message = "The field should be between 1 and 30 characters")
        String title,

        @NotNull(message = "Please provide parentId")
        Long parentId,

        String image) {

    @AssertTrue(message = "Provide product category id or title")
    private boolean isIdOrTitlePresented(){
        return id != null || parentId != null;
    }

}
