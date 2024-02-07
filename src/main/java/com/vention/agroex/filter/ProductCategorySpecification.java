package com.vention.agroex.filter;

import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.exception.InvalidArgumentException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class ProductCategorySpecification implements Specification<ProductCategoryEntity> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate
            (Root<ProductCategoryEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        String key = criteria.getKey();
        Object value = criteria.getValue();
        return switch (criteria.getOperation()) {
            case ">" -> builder.greaterThanOrEqualTo(
                    root.get(key), value.toString());
            case "<" -> builder.lessThanOrEqualTo(
                    root.get(key), value.toString());
            case ":" -> {
                if (root.get(key).getJavaType() == String.class) {
                    yield builder.like(root.get(key), "%" + value.toString().toLowerCase() + "%");
                } else {
                    yield builder.equal(root.get(key), value);
                }
            }
            default -> throw new InvalidArgumentException("Wrong filter parameter");
        };
    }
}
