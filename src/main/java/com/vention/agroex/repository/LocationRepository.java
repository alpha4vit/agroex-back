package com.vention.agroex.repository;

import com.vention.agroex.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findByCountryIdAndRegion(Long countryId, String region);
}
