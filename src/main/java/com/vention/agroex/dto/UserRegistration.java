package com.vention.agroex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistration {
    private UUID sub;
    private String name;
    private String email;
    private String zoneinfo;
}
