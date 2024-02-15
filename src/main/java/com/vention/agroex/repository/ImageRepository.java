package com.vention.agroex.repository;

import com.vention.agroex.entity.ImageEntity;
import com.vention.agroex.entity.LotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
    Optional<ImageEntity> findByName(String fileName);

    List<ImageEntity> findByLot(LotEntity lot);
}