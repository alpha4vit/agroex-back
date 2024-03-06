package com.vention.agroex.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record LotStatusResponse(Long lotId, UUID userid, BigDecimal betAmount, Instant betTime, String status) {
}
