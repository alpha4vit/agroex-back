package com.vention.agroex.repository;

import com.vention.agroex.entity.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ColorRepository extends JpaRepository<ColorEntity, Long> {

    @Query(value = "SELECT id, bg_color_hex, text_color_hex FROM colors WHERE id = (SELECT nextval('cycle_sequence'))", nativeQuery = true)
    ColorEntity findNextColor();
}
