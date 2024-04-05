package com.vention.agroex.util.updater;

import com.vention.agroex.client.CurrencyClient;
import com.vention.agroex.service.CurrencyRateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyUpdater {

    @Value(value = "${currency-api.key}")
    private String currencyKey;

    private final String EVERY_12_HOURS = "0 0 */12 * * *";

    private final CurrencyRateService currencyRateService;
    private final CurrencyClient currencyClient;


    @Scheduled(cron = EVERY_12_HOURS)
    @Transactional(rollbackOn = Exception.class)
    public void updateCurrencies() {
        log.info("Currency rates update started");
        var currencyNames = currencyRateService.getDistinctCurrencies();
        currencyNames.forEach(currencyName -> {
            var targets = currencyRateService.getTargetsByCurrency(currencyName);
            var updatedRates = currencyClient.getAllCurrenciesByBase(currencyName, currencyKey).quotes();
            targets.forEach(targetEntity -> targetEntity.setRate(
                    new BigDecimal(updatedRates.get(
                            currencyName + targetEntity.getTargetCurrency())
                    )));
        });
        log.info("Currency rates updated");
    }

}
