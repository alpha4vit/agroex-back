package com.vention.agroex.dto;

import jakarta.validation.constraints.AssertTrue;

public record ProductCategory(
        Long id,

        String title,

        Long parentId,

        String image) {

    @AssertTrue(message = "Provide product category id or title")
    private boolean isIdOrTitlePresented(){
        return id != null || parentId != null;
    }

}
