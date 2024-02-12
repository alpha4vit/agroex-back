package com.vention.agroex.repository;

import com.vention.agroex.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<LotEntity, Long> {
    Optional<LotEntity> findByTitle(String title);
}
