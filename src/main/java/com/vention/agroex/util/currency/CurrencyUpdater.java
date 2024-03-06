package com.vention.agroex.util.currency;

import com.vention.agroex.model.CurrencyExchange;
import com.vention.agroex.service.CurrencyRateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyUpdater {

    @Value(value = "${currency-api.key}")
    private String currencyKey;

    @Value(value = "${currency-api.url}")
    private String currencyUrl;

    private final CurrencyRateService currencyRateService;
    private final RestTemplate restTemplate;

    @Scheduled(cron = "0 0 */12 * * *")
    @Transactional(rollbackOn = Exception.class)
    public void updateCurrencies() {
        log.info("Currency rates update started");
        var currencyNames = currencyRateService.getDistinctCurrencies();
        currencyNames.forEach(currencyName -> {
            var targets = currencyRateService.getTargetsByCurrency(currencyName);
            var updatedRates = getAllCurrenciesByBase(currencyName);
            targets.forEach(targetEntity -> targetEntity.setRate(
                    new BigDecimal(updatedRates.get(
                            currencyName + targetEntity.getTargetCurrency())
                    )));
        });
        log.info("Currency rates updated");
    }

    public Map<String, String> getAllCurrenciesByBase(String base) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(currencyUrl)
                    .queryParam("apikey", currencyKey)
                    .queryParam("source", base);

            var response = restTemplate.getForObject(builder.toUriString(), CurrencyExchange.class);
            return response.quotes();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

}
