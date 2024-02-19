package com.vention.agroex.model;

import lombok.Data;

import java.time.Instant;

@Data
public class BetRequest {

    private Long id;

    private Long userId;

    private Long amount;

    private Instant betTime;
}
