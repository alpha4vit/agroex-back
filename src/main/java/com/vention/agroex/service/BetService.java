package com.vention.agroex.service;

import com.vention.agroex.entity.BetEntity;

import java.util.List;

public interface BetService {

    BetEntity makeBet(Long lotId, BetEntity betEntity);

    List<BetEntity> getLotBets(Long lotId);
}
