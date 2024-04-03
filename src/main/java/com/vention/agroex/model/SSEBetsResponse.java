package com.vention.agroex.model;

import com.vention.agroex.dto.Bet;

public record SSEBetsResponse(Bet bet, String lotStatus) {
}
