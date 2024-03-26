package com.vention.agroex.service;

import com.vention.agroex.entity.CurrencyRateEntity;

import java.util.List;
import java.util.Set;

public interface CurrencyRateService {

    List<CurrencyRateEntity> getAll();

    CurrencyRateEntity create(CurrencyRateEntity rate);

    void deleteById(Long id);

    Set<String> getDistinctCurrencies();

    Set<CurrencyRateEntity> getTargetsByCurrency(String currency);

    CurrencyRateEntity getByCurrencies(String source, String target);

}
