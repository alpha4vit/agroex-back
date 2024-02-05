package com.vention.agroex.repository;

import com.vention.agroex.entity.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {
    Optional<Lot> findByTitle(String title);
}
