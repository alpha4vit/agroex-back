package com.vention.agroex.util.validator;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.exception.InvalidArgumentException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

@Component
public class LotDTOValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Lot.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Map<String, String> errorsMap = new HashMap<>();
        errors.getFieldErrors()
                .forEach(error -> errorsMap.put(error.getField(), error.getDefaultMessage()));
        if (errorsMap.size() > 0)
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
    }
}
