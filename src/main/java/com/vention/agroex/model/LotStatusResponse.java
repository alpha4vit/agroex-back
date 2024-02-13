package com.vention.agroex.model;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record LotStatusResponse(Long lotId, UUID userid, Float betAmount, Instant betTime, String status) {
}
