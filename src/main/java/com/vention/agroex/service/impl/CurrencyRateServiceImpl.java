package com.vention.agroex.service.impl;

import com.vention.agroex.entity.CurrencyRateEntity;
import com.vention.agroex.repository.CurrencyRateRepository;
import com.vention.agroex.service.CurrencyRateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CurrencyRateServiceImpl implements CurrencyRateService{

    private final CurrencyRateRepository currencyRateRepository;

    @Override
    public List<CurrencyRateEntity> getAll() {
        return currencyRateRepository.findAll();
    }

    @Override
    public Set<String> getDistinctCurrencies() {
        return currencyRateRepository.findDistinctCurrencies();
    }

    @Override
    public Set<CurrencyRateEntity> getTargetsByCurrency(String currency) {
        return currencyRateRepository.findBySourceCurrency(currency);
    }

    @Override
    public CurrencyRateEntity getByCurrencies(String source, String target) {
        return currencyRateRepository.findBySourceCurrencyAndTargetCurrency(source, target)
                .orElseThrow(() -> new EntityNotFoundException("Currency rate not found!"));
    }

}
