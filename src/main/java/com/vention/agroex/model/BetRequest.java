package com.vention.agroex.model;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record BetRequest(UUID userId, Float amount, Instant betTime) {}
