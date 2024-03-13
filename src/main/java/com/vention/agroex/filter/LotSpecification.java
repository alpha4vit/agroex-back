package com.vention.agroex.filter;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidArgumentException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class LotSpecification implements Specification<LotEntity> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NotNull Root<LotEntity> root,
                                 @NotNull CriteriaQuery<?> query,
                                 @NotNull CriteriaBuilder builder) {

        String key = criteria.getKey();
        Object value = criteria.getValue();
        return switch (key) {
            case ("productCategory") -> builder.equal(root.join("productCategory").get("id"), value);
            case ("location") -> builder.equal(root.join("location").get("id"), value);
            case ("user") -> builder.equal(root.join("user").get("id"), value);
            case ("price") -> getPredicateFromOperation(criteria.getOperation(), root, "calculatedPrice", value, builder);
            case ("country") -> builder.equal(root.join("location").join("country").get("id"), value);
            case ("region") -> builder.equal(root.join("location").get("region"), value);
            default -> getPredicateFromOperation(criteria.getOperation(), root, key, value, builder);
        };
    }


    private Predicate getPredicateFromOperation(String operation, Root<LotEntity> root, String key, Object value,
                                                CriteriaBuilder builder) {
        return switch (operation) {
            case ">" -> builder.greaterThanOrEqualTo(
                    root.get(key), value.toString());
            case "<" -> builder.lessThanOrEqualTo(
                    root.get(key), value.toString());
            case ":" -> {
                if (root.get(key).getJavaType() == String.class) {
                    yield builder.equal(builder.lower(root.get(key)), value.toString().toLowerCase());
                } else {
                    yield builder.equal(root.get(key), value);
                }
            }
            default -> throw new InvalidArgumentException("Wrong filter parameter");
        };
    }
}
