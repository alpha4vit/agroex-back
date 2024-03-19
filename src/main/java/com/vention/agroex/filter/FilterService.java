package com.vention.agroex.filter;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.service.ProductCategoryService;
import com.vention.agroex.util.constant.StatusConstants;
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

    private final ProductCategoryService productCategoryService;

    private final String EQUALS = ":";
    private final String GREATER = ">";
    private final String LOWER = "<";

    public Specification<LotEntity> getCriteria(Map<String, String> filters, String currency) {

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

        var nonNullFilters = filters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (!nonNullFilters.containsKey("enabledByAdmin")) {
            mainFieldsBuilder.with("enabledByAdmin", EQUALS, true, currency);
        }

        if (!nonNullFilters.containsKey("status")) {
            statusBuilder.with("status", EQUALS, StatusConstants.ACTIVE, currency);
        }
        nonNullFilters.forEach((field, value) -> {
            switch (field) {
                case "title" -> mainFieldsBuilder.with("title", EQUALS, value, currency);
                case "minQuantity" -> mainFieldsBuilder.with("quantity", GREATER, Float.parseFloat(value), currency);
                case "maxQuantity" -> mainFieldsBuilder.with("quantity", LOWER, Float.parseFloat(value), currency);
                case "minPrice" -> mainFieldsBuilder.with("price", GREATER, Float.parseFloat(value), currency);
                case "price" -> mainFieldsBuilder.with("price", EQUALS, Float.parseFloat(value), currency);
                case "maxPrice" -> mainFieldsBuilder.with("price", LOWER, Float.parseFloat(value), currency);
                case "enabledByAdmin" -> mainFieldsBuilder.with("enabledByAdmin", EQUALS, Boolean.parseBoolean(value), currency);
                case "keyword" -> mainFieldsBuilder.with("keyword", EQUALS, value, currency);
                case "categories", "subcategories" -> {
                    if (!nonNullFilters.containsKey("subcategories") || field.equals("subcategories"))
                        getStringStream(value)
                                .flatMap(string -> productCategoryService.getAllSubCategories(Long.parseLong(string)).stream())
                                .forEach(category -> productCategoryBuilder.with("productCategory", EQUALS, category.getId(), currency));
                }
                case "users" -> getStringStream(value)
                        .map(UUID::fromString)
                        .forEach(user -> userBuilder.with("user", EQUALS, user, currency));
                case "lotType" -> getStringStream(value)
                        .forEach(type -> typeBuilder.with("lotType", EQUALS, type, currency));
                case "countries" -> getStringStream(value)
                        .map(Long::parseLong)
                        .forEach(country -> countryBuilder.with("country", EQUALS, country, currency));
                case "regions" -> getStringStream(value)
                        .forEach(region -> regionBuilder.with("region", EQUALS, region, currency));
                case "innerStatus" -> getStringStream(value)
                        .forEach(status -> adminStatusBuilder.with("innerStatus", EQUALS, status, currency));
                case "status" -> getStringStream(value)
                        .filter(status -> !status.equals("all"))
                        .forEach(status -> statusBuilder.with("status", EQUALS, status, currency));
                case "userStatus" -> getStringStream(value)
                        .forEach(status -> userStatusBuilder.with("userStatus", EQUALS, status, currency));
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

        return Stream.of(mainSpec, categorySpec, userSpec, typeSpec, countrySpec, regionSpec,
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
}
