package com.vention.agroex.util.validator;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.service.CurrencyRateService;
import com.vention.agroex.util.constant.LotTypeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> map = new HashMap<>();
        errors.getFieldErrors()
                .forEach((fieldError) -> {
                            String field = fieldError.getField();
                            if (field.startsWith("productCategory")) {
                                map.put("productCategory", fieldError.getDefaultMessage());
                            } else if (field.startsWith("location")) {
                                String fieldName = field.substring(9);
                                ((Map<String, String>) map.computeIfAbsent("location", k -> new HashMap<String, String>()))
                                        .put(fieldName, fieldError.getDefaultMessage());
                            } else
                                map.put(field, fieldError.getDefaultMessage());
                        }
                );
        var lot = (Lot) target;
        if (lot.getLotType() != null && lot.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            if (lot.getDuration() == null) {
                map.put("duration", "Please fill in the field");
            } else if (lot.getDuration() < 600000L) {
                map.put("duration", "Duration must be more than 10 minutes");
            }
            if (lot.getOriginalMinPrice() == null) {
                map.put("originalMinPrice", "Please fill in the field");
            }
            if (lot.getOriginalMinPrice() != null &&
                    lot.getOriginalMinPrice().compareTo(lot.getOriginalPrice()) > 0) {
                map.put("originalMinPrice", "Min price can`t be bigger than lot price");
            }
        }
        if (lot.getLotType() != null && (lot.getLotType().equals(LotTypeConstants.BUY) || lot.getLotType().equals(LotTypeConstants.SELL))){
            if (lot.getExpirationDate() == null)
                map.put("expirationDate", "Please fill in the field");
        }
        var currencies = currencyRateService.getDistinctCurrencies();
        if (!currencies.contains(lot.getOriginalCurrency()))
            map.put("originalCurrency", "Unsupported currency presented!");

        if (!map.isEmpty())
            throw new InvalidArgumentException(map, "Invalid arguments!");
    }

}
