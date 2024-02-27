package com.vention.agroex.util.updater;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.constant.StatusConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class LotStatusUpdater {

    private final LotService lotService;

    @Scheduled(fixedRate = 1000)
    public void lotExpirationCheck() {
        var lots = lotService.getAll();
        lots.forEach(this::checkStatus);
    }

    private void checkStatus(LotEntity lot) {
        if (!lot.getStatus().equals(StatusConstants.EXPIRED)) {
            var now = Instant.now();
            var lotExpirationDate = lot.getExpirationDate();
            if (lotExpirationDate.isBefore(now)) {
                if (lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
                    log.info(String.format("Lot with id %d finished by time", lot.getId()));
                    lotService.finishAuction(lot);
                } else {
                    log.info(String.format("Lot with id %d expired", lot.getId()));
                    lot.setStatus(StatusConstants.EXPIRED);
                    lotService.update(lot.getId(), lot);
                }
            }
        }
    }
}
