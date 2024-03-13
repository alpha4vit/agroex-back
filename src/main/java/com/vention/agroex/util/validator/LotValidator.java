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
public class LotValidator implements Validator {

    private final CurrencyRateService currencyRateService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Lot.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        record ErrorField(String field, String message) {}

        Map<String, String> map = errors.getFieldErrors().stream()
                .map(fieldError -> {
                            String field = fieldError.getField();
                            if (field.startsWith("productCategory"))
                                field = "productCategory";
                            return new ErrorField(field, fieldError.getDefaultMessage());
                        }
                )
                .collect(toMap(ErrorField::field, ErrorField::message));

        var lot = (Lot) target;
        if (lot.getLotType() != null && lot.getLotType().equals("auctionSell")) {
            if (lot.getDuration() == null) {
                map.put("duration", "Duration of auction lot should not be null!");
            } else if (lot.getDuration() < 600000L) {
                map.put("duration", "Duration must be more than 10 minutes");
            }
            if (lot.getMinPrice().compareTo(lot.getOriginalPrice()) > 0) {
                map.put("minPrice", "Min price can`t be bigger than lot price");
            }
        }
        var currencies = currencyRateService.getDistinctCurrencies();
        if (!currencies.contains(lot.getOriginalCurrency()))
            map.put("originalCurrency", "Unsupported currency presented!");

        if (!map.isEmpty())
            throw new InvalidArgumentException(map, "Invalid arguments!");
    }
}
