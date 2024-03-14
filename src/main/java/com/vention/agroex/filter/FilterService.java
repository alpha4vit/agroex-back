package com.vention.agroex.filter;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.util.constant.StatusConstants;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FilterService {
    private static String keyword;
    private final String EQUALS = ":";
    private final String GREATER = ">";
    private final String LOWER = "<";

    public Specification<LotEntity> getCriteria(Map<String, String> filters) {

        clearNonSearchFields(filters);

        var mainFieldsBuilder = new LotSpecificationsBuilder();
        var productCategoryBuilder = new LotSpecificationsBuilder();
        var userBuilder = new LotSpecificationsBuilder();
        var typeBuilder = new LotSpecificationsBuilder();
        var countryBuilder = new LotSpecificationsBuilder();
        var userStatusBuilder = new LotSpecificationsBuilder();
        var adminStatusBuilder = new LotSpecificationsBuilder();
        var statusBuilder = new LotSpecificationsBuilder();
        var regionBuilder = new LotSpecificationsBuilder();
        keyword = "";

        var nonNullFilters = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!nonNullFilters.containsKey("enabledByAdmin")) {
            mainFieldsBuilder.with("enabledByAdmin", EQUALS, true);
        }

        if (!nonNullFilters.containsKey("status")){
            statusBuilder.with("status", EQUALS, StatusConstants.ACTIVE);
        }

        nonNullFilters.forEach((field, value) -> {
            switch (field) {
                case "title" -> mainFieldsBuilder.with("title", EQUALS, value);
                case "minQuantity" -> mainFieldsBuilder.with("quantity", GREATER, Float.parseFloat(value));
                case "maxQuantity" -> mainFieldsBuilder.with("quantity", LOWER, Float.parseFloat(value));
                case "enabledByAdmin" -> mainFieldsBuilder.with("enabledByAdmin", EQUALS, Boolean.parseBoolean(value));

                case "categories" -> getStringStream(value)
                        .map(Long::parseLong)
                        .forEach(category -> productCategoryBuilder.with("productCategory", EQUALS, category));
                case "users" -> getStringStream(value)
                        .map(UUID::fromString)
                        .forEach(user -> userBuilder.with("user", EQUALS, user));
                case "lotType" -> getStringStream(value)
                        .forEach(type -> typeBuilder.with("lotType", EQUALS, type));
                case "countries" -> getStringStream(value)
                        .map(Long::parseLong)
                        .forEach(country -> countryBuilder.with("country", EQUALS, country));
                case "regions" -> getStringStream(value)
                        .forEach(region -> regionBuilder.with("region", EQUALS, region));
                case "innerStatus" -> getStringStream(value)
                        .forEach(status -> adminStatusBuilder.with("innerStatus", EQUALS, status));
                case "status" -> getStringStream(value)
                        .filter(status -> !status.equals("all"))
                        .forEach(status -> statusBuilder.with("status", EQUALS, status));
                case "userStatus" -> getStringStream(value)
                        .forEach(status -> userStatusBuilder.with("userStatus", EQUALS, status));
                case "keyword" -> keyword = value;
                case "minPrice", "maxPrice" -> {}
                default -> throw new InvalidArgumentException(
                        String.format("There are no parameters with name %s", field));
            }
        });

        var mainSpec = mainFieldsBuilder.buildAnd();
        var categorySpec = productCategoryBuilder.buildOr();
        var userSpec = userBuilder.buildOr();
        var typeSpec = typeBuilder.buildOr();
        var countrySpec = countryBuilder.buildOr();
        var regionSpec = regionBuilder.buildOr();
        var userStatusSpec = userStatusBuilder.buildOr();
        var statusSpec = statusBuilder.buildOr();
        var adminsStatusSpec = adminStatusBuilder.buildOr();
        var keywordPredicateSpec = (Specification<LotEntity>) (root, query, criteriaBuilder) ->
                getKeywordPredicate(criteriaBuilder, root, keyword);

        return Stream.of(mainSpec, categorySpec, userSpec, typeSpec, countrySpec, regionSpec, keywordPredicateSpec,
                        userStatusSpec, statusSpec, adminsStatusSpec)
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
    }

    @NotNull
    private static Stream<String> getStringStream(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim);
    }

    private void clearNonSearchFields(Map<String, String> filters) {
        filters.remove("pageNumber");
        filters.remove("pageSize");
    }

    private Predicate getKeywordPredicate(CriteriaBuilder criteriaBuilder, Root<LotEntity> root, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            var keywordsArray = Arrays.stream(keyword.split(" ")).toList();
            Expression<String> searchField = root.get("searchString");
            var keywordPredicates = keywordsArray.stream()
                    .map(word -> {
                        String lowerCasedWord = word.toLowerCase(Locale.ROOT);
                        return criteriaBuilder.like(searchField, "%" + lowerCasedWord + "%");
                    }).toList();
            return criteriaBuilder.and(keywordPredicates.toArray(new Predicate[0]));
        } else {
            return criteriaBuilder.conjunction();
        }
    }
}
