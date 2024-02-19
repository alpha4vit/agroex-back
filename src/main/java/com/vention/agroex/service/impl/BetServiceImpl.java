package com.vention.agroex.service.impl;

import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidBetException;
import com.vention.agroex.repository.BetRepository;
import com.vention.agroex.service.BetService;
import com.vention.agroex.service.LotService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetServiceImpl implements BetService {

    private final LotService lotService;
    private final BetRepository betRepository;

    @Override
    @Transactional
    public BetEntity makeBet(Long lotId, BetEntity betEntity) {
        var lot = lotService.getById(lotId);

        saveBet(lot, betEntity);

        return betEntity;
    }

    private void saveBet(LotEntity lot, BetEntity betEntity) {
        var bets = lot.getBets();

        bets.stream().max(Comparator.comparing(BetEntity::getBetTime))
                .ifPresent(lastBet -> {
                    if (lastBet.getAmount() >= betEntity.getAmount()) {
                        throw new InvalidBetException(
                                String.format("Your bet amount must be higher than the last one: %d", lastBet.getAmount()));
                    }
                });

        bets.addFirst(betEntity);
        lot.setBets(bets);
        lotService.update(lot.getId(), lot);
    }

    @Override
    public List<BetEntity> getLotBets(Long lotId) {
        return betRepository.findByLotId(lotId);
    }
}
