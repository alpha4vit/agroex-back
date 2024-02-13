package com.vention.agroex.model;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class BetRequest {

    private Long id;

    private UUID userId;

    private Float amount;

    private Instant betTime;
}
