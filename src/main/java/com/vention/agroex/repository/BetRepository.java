package com.vention.agroex.repository;

import com.vention.agroex.entity.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetRepository extends JpaRepository<BetEntity, Long> {
    List<BetEntity> findByLotId(Long lotId);
}
