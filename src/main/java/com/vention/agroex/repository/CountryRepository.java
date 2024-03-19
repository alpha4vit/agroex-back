package com.vention.agroex.repository;

import com.vention.agroex.entity.CountryEntity;
import com.vention.agroex.model.CountryReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {
    Optional<CountryEntity> findByName(String name);
    List<CountryEntity> findByLocationsIsNotEmpty();

    @Query(value = "select * from country_filter_by_lot_price(:startDate, :expirationDate, :lotType)", nativeQuery = true)
    List<CountryReportModel> filterByLotPrice(@Param("startDate") ZonedDateTime startDate,
                                              @Param("expirationDate") ZonedDateTime expirationDate,
                                              @Param("lotType") @Nullable String lotType);

    @Query(value = "select * from country_filter_by_lot_count(:startDate, :expirationDate, :lotType)", nativeQuery = true)
    List<CountryReportModel> filterByLotCount(@Param("startDate") ZonedDateTime startDate,
                                                     @Param("expirationDate") ZonedDateTime expirationDate,
                                                     @Param("lotType") @Nullable String lotType);

    @Query(value = "select * from country_filter_by_owner_count(:startDate, :expirationDate, :lotType)", nativeQuery = true)
    List<CountryReportModel> filterByOwnerCount(@Param("startDate") ZonedDateTime startDate,
                                                     @Param("expirationDate") ZonedDateTime expirationDate,
                                                     @Param("lotType") @Nullable String lotType);

    @Query(value = "select * from country_owner_filter_by_bets(:startDate, :expirationDate, :lotType)", nativeQuery = true)
    List<CountryReportModel> filterByOwnersLotsBets(@Param("startDate") ZonedDateTime startDate,
                                                        @Param("expirationDate") ZonedDateTime expirationDate,
                                                        @Param("lotType") @Nullable String lotType);

    @Query(value = "select * from country_filter_by_participant_count(:startDate, :expirationDate, :lotType)", nativeQuery = true)
    List<CountryReportModel> filterByParticipantCount(@Param("startDate") ZonedDateTime startDate,
                                                                @Param("expirationDate") ZonedDateTime expirationDate,
                                                                @Param("lotType") @Nullable String lotType);

    @Query(value = "select * from country_participant_filter_by_bets(:startDate, :expirationDate, :lotType)", nativeQuery = true)
    List<CountryReportModel> filterByParticipantBets(@Param("startDate") ZonedDateTime startDate,
                                                      @Param("expirationDate") ZonedDateTime expirationDate,
                                                      @Param("lotType") @Nullable String lotType);
}
