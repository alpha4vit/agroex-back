package com.vention.agroex.service.impl;

import com.vention.agroex.controller.SSEBetsController;
import com.vention.agroex.dto.Notification;
import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidBetException;
import com.vention.agroex.repository.BetRepository;
import com.vention.agroex.service.BetService;
import com.vention.agroex.service.LotService;
import com.vention.agroex.service.NotificationService;
import com.vention.agroex.util.constant.NotificationReadStatusConstants;
import com.vention.agroex.util.constant.Role;
import com.vention.agroex.util.constant.StatusConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BetServiceImpl implements BetService {

    private final LotService lotService;
    private final NotificationService notificationService;
    private final SSEBetsController sseBetsController;
    private final BetRepository betRepository;

    @Override
    @Transactional
    public BetEntity makeBet(Long lotId, BetEntity betEntity, String currency) {
        var lot = lotService.getById(lotId, currency);

        validateBet(betEntity, lot);
        saveBet(lot, betEntity);
        if (betEntity.getAmount().compareTo(lot.getOriginalPrice()) == 0) {
            log.info(String.format("User with id %s made a maxPrice bet. Auction ended",
                    betEntity.getUser().getId()));
            lotService.finishAuction(lot);
        }
        betEntity.setBetTime(Instant.now());
        sseBetsController.sendBet(lot.getId(), betEntity, lot.getStatus());
        return betEntity;
    }

    private void validateBet(BetEntity betEntity, LotEntity lot) {
        if (betEntity.getAmount().subtract(lot.getOriginalMinPrice()).compareTo(new BigDecimal(1)) < 0) {
            throw new InvalidBetException("The minimum price step is one conventional unit");
        }
        if (betEntity.getAmount().compareTo(lot.getOriginalPrice()) > 0) {
            throw new InvalidBetException("You can't make bet with amount, which is bigger than lot price");
        }
        if (!lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            throw new InvalidBetException("This lot is not an auction lot");
        }
        if (lot.getStatus().equals(StatusConstants.FINISHED)) {
            throw new InvalidBetException("This auction is already finished");
        }
        if (!lot.getStatus().equals(StatusConstants.ACTIVE)) {
            throw new InvalidBetException("This auction is not active. You can`t make bets now");
        }
        if (lot.getUser().getId() == betEntity.getUser().getId()) {
            throw new InvalidBetException("You can't make bets in your own auction");
        }
        if (lot.getMinPrice() != null && lot.getOriginalMinPrice().compareTo(betEntity.getAmount()) >= 0) {
            throw new InvalidBetException(
                    String.format("Your bet must be higher than the minimal price: %f", lot.getMinPrice()));
        }
    }

    private void saveBet(LotEntity lot, BetEntity betEntity) {
        var bets = lot.getBets();


        bets.stream().max(Comparator.comparing(BetEntity::getBetTime))
                .ifPresent(lastBet -> {
                    if (betEntity.getAmount().subtract(lastBet.getAmount()).compareTo(new BigDecimal(1)) < 0) {
                        throw new InvalidBetException(
                                String.format("Your bet amount must be 1 conventional point higher than the last one: %.2f %s", lastBet.getAmount(), lot.getOriginalCurrency()));
                    }
                });

        var lastBet = bets.stream()
                .max(Comparator.comparing(BetEntity::getAmount))
                .orElse(null);

        if (lastBet != null && !lastBet.getUser().getId().equals(betEntity.getUser().getId())) {
            notificationService.save(new Notification(
                    UUID.randomUUID(),
                    lastBet.getUser().getId(),
                    lot.getId(),
                    "BET_OUTBID",
                    "Your bet was outbid",
                    String.format("Your bet was outbid. Auction id: %d New bet amount: %.2f %s", lot.getId(), betEntity.getAmount(), lot.getOriginalCurrency()),
                    NotificationReadStatusConstants.UNREAD,
                    Instant.now(),
                    Role.USER
            ));
        }

        bets.addFirst(betEntity);
        lot.setBets(bets);
        lotService.update(lot.getId(), lot);
    }

    @Override
    public List<BetEntity> getLotBets(Long lotId) {
        return betRepository.findByLotId(lotId);
    }
}