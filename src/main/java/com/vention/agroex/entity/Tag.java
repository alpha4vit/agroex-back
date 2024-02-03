package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Embeddable
public class Tag {

    private String title;
}
