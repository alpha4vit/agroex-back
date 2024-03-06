package com.vention.agroex.repository;

import com.vention.agroex.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotRepository extends JpaRepository<LotEntity, Long>, JpaSpecificationExecutor<LotEntity>, PagingAndSortingRepository<LotEntity, Long> {
    List<LotEntity> findByBetsUserId(UUID userId);
}
