package com.vention.agroex.client;

import com.vention.agroex.model.CurrencyExchange;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "currency", url = "${currency-api.url}")
public interface CurrencyClient {

    @GetMapping
    CurrencyExchange getAllCurrenciesByBase(@RequestParam("source") String base,
                                            @RequestParam("apikey") String apikey);

}
