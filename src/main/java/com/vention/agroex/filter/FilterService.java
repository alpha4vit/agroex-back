package com.vention.agroex.filter;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.repository.LotRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FilterService {
    private static String keyword;
    private final String EQUALS = ":";
    private final String GREATER = ">";
    private final String LOWER = "<";

    private final LotRepository lotRepository;

    public List<LotEntity> getWithCriteria(Map<String, String> filters) {
        var mainFieldsBuilder = new LotSpecificationsBuilder();
        var productCategoryBuilder = new LotSpecificationsBuilder();
        var userBuilder = new LotSpecificationsBuilder();
        var typeBuilder = new LotSpecificationsBuilder();
        var countryBuilder = new LotSpecificationsBuilder();
        keyword = "";

        filters.forEach((field, value) -> {
            switch (field) {
                case "title" -> mainFieldsBuilder.with("title", EQUALS, value);
                case "minQuantity" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(Float::parseFloat)
                        .ifPresent(minQuantity -> mainFieldsBuilder.with("quantity", GREATER, minQuantity));
                case "maxQuantity" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(Float::parseFloat)
                        .ifPresent(maxQuantity -> mainFieldsBuilder.with("quantity", LOWER, maxQuantity));
                case "minPrice" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(Float::parseFloat)
                        .ifPresent(minPrice -> mainFieldsBuilder.with("price", GREATER, minPrice));
                case "maxPrice" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(Float::parseFloat)
                        .ifPresent(maxPrice -> mainFieldsBuilder.with("price", LOWER, maxPrice));
                case "enabledByAdmin" -> {
                    var enabledByAdmin = value.isEmpty() || Boolean.parseBoolean(value);
                    mainFieldsBuilder.with("enabledByAdmin", EQUALS, enabledByAdmin);
                }

                case "categories" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(v -> Arrays.stream(v.split(","))
                                .map(String::trim)
                                .map(Long::parseLong)
                                .toList())
                        .ifPresent(categories -> categories.forEach(category ->
                                productCategoryBuilder.with("productCategory", EQUALS, category)
                        ));

                case "users" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(v -> Arrays.stream(v.split(","))
                                .map(String::trim)
                                .map(Long::parseLong)
                                .toList())
                        .ifPresent(users -> users.forEach(user ->
                                userBuilder.with("user", EQUALS, user)
                        ));

                case "lotType" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(v -> Arrays.stream(v.split(","))
                                .map(String::trim)
                                .map(String::toString)
                                .toList())
                        .ifPresent(types -> types.forEach(type ->
                                typeBuilder.with("lotType", EQUALS, type)
                        ));

                case "countries" -> Optional.ofNullable(value)
                        .filter(v -> !v.isEmpty())
                        .map(v -> Arrays.stream(v.split(","))
                                .map(String::trim)
                                .map(Long::parseLong)
                                .toList())
                        .ifPresent(countries -> countries.forEach(country ->
                                countryBuilder.with("country", EQUALS, country)
                        ));

                case "keyword" -> keyword = value;

                default -> throw new InvalidArgumentException(
                        String.format("There are no parameters with name %s", field));
            }
        });

        var mainSpec = mainFieldsBuilder.buildAnd();
        var categorySpec = productCategoryBuilder.buildOr();
        var userSpec = userBuilder.buildOr();
        var typeSpec = typeBuilder.buildOr();
        var countrySpec = countryBuilder.buildOr();
        var keywordPredicateSpec = (Specification<LotEntity>) (root, query, criteriaBuilder) ->
                getKeywordPredicate(criteriaBuilder, root, keyword);

        var resultingSpec = Stream.of(mainSpec, categorySpec, userSpec, typeSpec, countrySpec, keywordPredicateSpec)
                .filter(Objects::nonNull)
                .reduce(Specification::and)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());
        return lotRepository.findAll(resultingSpec);
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
