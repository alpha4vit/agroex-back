package com.vention.agroex.filter;

import com.vention.agroex.entity.LotEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class LotSpecificationsBuilder {

    private final List<SearchCriteria> params = new ArrayList<>();

    public LotSpecificationsBuilder with(String key, String operation, Object value, String currency) {
        params.add(new SearchCriteria(key, operation, value, currency));
        return this;
    }

    public Specification<LotEntity> buildAnd() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<LotEntity> result = new LotSpecification(params.getFirst());

        for (int i = 1; i < params.size(); i++) {
            result = result.and(new LotSpecification(params.get(i)));
        }

        return result;
    }

    public Specification<LotEntity> buildOr() {
        if (params.isEmpty()) {
            return null;
        }

        Specification<LotEntity> result = new LotSpecification(params.getFirst());

        for (int i = 1; i < params.size(); i++) {
            result = result.or(new LotSpecification(params.get(i)));
        }

        return result;
    }
}