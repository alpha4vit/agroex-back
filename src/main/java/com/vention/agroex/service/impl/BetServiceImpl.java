package com.vention.agroex.service.impl;

import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidBetException;
import com.vention.agroex.repository.BetRepository;
import com.vention.agroex.service.BetService;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.constant.StatusConstants;
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
    public BetEntity makeBet(Long lotId, BetEntity betEntity) {
        var lot = lotService.getById(lotId);
        validateBet(betEntity, lot);
        if (betEntity.getAmount() >= lot.getPrice()) {
            log.info(String.format("User with id %s made a maxPrice bet. Auction ended",
                    betEntity.getUser().getId()));
            betEntity.setAmount(lot.getPrice());
            lotService.finishAuction(lot);
        }
        saveBet(lot, betEntity);
        return betEntity;
    }

    private void validateBet(BetEntity betEntity, LotEntity lot) {
        if (!lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            throw new InvalidBetException("This lot is not an auction lot");
        }
        if (lot.getStatus().equals(StatusConstants.FINISHED)) {
            throw new InvalidBetException("This auction is already finished");
        }
        if (lot.getMinPrice() > betEntity.getAmount()) {
            throw new InvalidBetException(
                    String.format("Your bet must be higher than the minimal price: %f", lot.getMinPrice()));
        }
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