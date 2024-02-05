package com.vention.agroex.util.validator;

import com.vention.agroex.dto.CountryDTO;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CountryDTOValidator implements Validator {

    private final CountryRepository countryRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CountryDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CountryDTO countryDTO = (CountryDTO) target;
        Map<String, String> errorsMap = new HashMap<>();
        if (countryRepository.findByName(countryDTO.getName()).isPresent())
            errorsMap.put("title", "Country with this title already exists!");
        errors.getFieldErrors()
                .forEach(error -> errorsMap.put(error.getField(), error.getDefaultMessage()));
        if (errorsMap.size() > 0)
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
    }
}
