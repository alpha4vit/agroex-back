package com.vention.agroex.repository;

import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.model.LotReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LotRepository extends JpaRepository<LotEntity, Long>, JpaSpecificationExecutor<LotEntity>, PagingAndSortingRepository<LotEntity, Long> {
    Optional<LotEntity> findByTitle(String title);
    List<LotEntity> findByUser(UserEntity user);
    Boolean existsByUser(UserEntity user);
    Boolean existsByBetsUserIdAndStatus(UUID userId, String status);
    List<LotEntity> findByBetsUserId(UUID userId);

    @Query(value = "select * from base_lot_filter(:startDate, :expirationDate, :lotType, :countryId)", nativeQuery = true)
    List<LotReportModel> baseLotFilter(@Param("startDate") ZonedDateTime startDate,
                                       @Param("expirationDate") ZonedDateTime expirationDate,
                                       @Param("lotType") @Nullable String lotType,
                                       @Param("countryId") @Nullable Long countryId);

    @Query(value = "select * from lot_filter_by_price(:startDate, :expirationDate, :lotType, :countryId)", nativeQuery = true)
    List<LotReportModel> filterByPrice(@Param("startDate") ZonedDateTime startDate,
                                       @Param("expirationDate") ZonedDateTime expirationDate,
                                       @Param("lotType") @Nullable String lotType,
                                       @Param("countryId") @Nullable Long countryId);

    Boolean existsByProductCategoryId(Long prodcuctCategoryId);

}
