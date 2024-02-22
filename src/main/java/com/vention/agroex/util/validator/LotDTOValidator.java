package com.vention.agroex.util.validator;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
@RequiredArgsConstructor
public class LotDTOValidator implements Validator {

    private final CurrencyRateService currencyRateService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Lot.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        record ErrorField(String field, String message){}

        Map<String, String> map = errors.getFieldErrors().stream()
                .map(fieldError -> new ErrorField(
                        fieldError.getField(), fieldError.getDefaultMessage())
                )
                .collect(toMap(ErrorField::field, ErrorField::message));

        var lot = (Lot) target;
        var currencies = currencyRateService.getDistinctCurrencies();
        if (!currencies.contains(lot.getOriginalCurrency()))
            map.put("originalCurrency", "Unsupported currency presented!");

        if (!map.isEmpty())
            throw new InvalidArgumentException(map, "Invalid arguments!");
    }
}
