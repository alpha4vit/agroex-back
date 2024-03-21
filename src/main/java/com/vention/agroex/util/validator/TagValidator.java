package com.vention.agroex.util.validator;

import com.vention.agroex.dto.Tag;
import com.vention.agroex.exception.InvalidArgumentException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Component
public class TagValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Tag.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        record ErrorField(String field, String message){}

        Map<String, Object> map = errors.getFieldErrors().stream()
                .map(fieldError -> new ErrorField(
                        fieldError.getField(), fieldError.getDefaultMessage())
                )
                .collect(toMap(ErrorField::field, ErrorField::message));

        if (!map.isEmpty())
            throw new InvalidArgumentException(map, "Invalid arguments!");
    }
}
