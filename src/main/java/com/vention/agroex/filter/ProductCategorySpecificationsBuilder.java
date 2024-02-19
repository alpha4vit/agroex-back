package com.vention.agroex.filter;

import com.vention.agroex.entity.ProductCategoryEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class ProductCategorySpecificationsBuilder {

    private final List<SearchCriteria> params = new ArrayList<>();

    public ProductCategorySpecificationsBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<ProductCategoryEntity> build() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<ProductCategoryEntity> result = new ProductCategorySpecification(params.getFirst());

        for (int i = 1; i < params.size(); i++) {
            result = result.and(new ProductCategorySpecification(params.get(i)));
        }

        return result;
    }
}