package com.vention.agroex.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record BetRequest(UUID userId, BigDecimal amount, Instant betTime) {}
