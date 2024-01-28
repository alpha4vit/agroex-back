package com.vention.agroex.util.validator;

import com.vention.agroex.entity.ProductCategory;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductCategoryValidator implements Validator {

    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return ProductCategory.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ProductCategory productCategory = (ProductCategory) target;
        Map<String, String> errorsMap = new HashMap<>();
        if (productCategoryRepository.findByTitle(productCategory.getTitle()).isPresent())
            errorsMap.put("title", "Product category with this title already exists");
        errors.getFieldErrors()
                .forEach(error -> errorsMap.put(error.getField(), error.getDefaultMessage()));
        if (errorsMap.size() > 0)
            throw new InvalidArgumentException(errorsMap, "Invalid arguments!");
    }
}
