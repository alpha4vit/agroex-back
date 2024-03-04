package com.vention.agroex.repository;

import com.vention.agroex.entity.UserEntity;
import com.vention.agroex.model.UserReportModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByUsernameOrEmail(String username, String email);
    Boolean existsByEmail(String email);

    @Query(value = "select * from user_filter_by_lot_count(:startDate, :expirationDate, :lotType, :countryId)", nativeQuery = true)
    List<UserReportModel> filterByLotCount(@Param("startDate") ZonedDateTime startDate,
                                           @Param("expirationDate") ZonedDateTime expirationDate,
                                           @Param("lotType") @Nullable String lotType,
                                           @Param("countryId") @Nullable Long countryId);

    @Query(value = "select * from user_filter_by_bet_money(:startDate, :expirationDate, :lotType, :countryId)", nativeQuery = true)
    List<UserReportModel> filterByBetMoney(@Param("startDate") ZonedDateTime startDate,
                                           @Param("expirationDate") ZonedDateTime expirationDate,
                                           @Param("lotType") @Nullable String lotType,
                                           @Param("countryId") @Nullable Long countryId);

    @Query(value = "select * from user_filter_by_lot_money(:startDate, :expirationDate, :lotType, :countryId)", nativeQuery = true)
    List<UserReportModel> filterByLotMoney(@Param("startDate") ZonedDateTime startDate,
                                           @Param("expirationDate") ZonedDateTime expirationDate,
                                           @Param("lotType") @Nullable String lotType,
                                           @Param("countryId") @Nullable Long countryId);
}
