package com.vention.agroex.model;

import lombok.Builder;

import java.time.Instant;

@Builder
public record LotStatusResponse(Long lotId, Long userid, Float betAmount, Instant betTime, String status) {
}
