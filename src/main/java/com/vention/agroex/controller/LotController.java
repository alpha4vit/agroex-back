package com.vention.agroex.controller;

import com.vention.agroex.dto.LotDTO;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.mapper.LotMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
@Tag(name = "Lot controller")
public class LotController {

    private final LotService lotService;
    private final LotMapper lotMapper;

    @PostMapping
    public ResponseEntity<LotDTO> save(@RequestBody LotDTO lotDTO) {
        Lot entity = lotMapper.toEntity(lotDTO);
        entity = lotService.save(entity);
        return ResponseEntity.ok(lotMapper.toDTO(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotDTO> update(@PathVariable Long id, @RequestBody LotDTO lotDTO) {
        Lot entity = lotService.getById(id);
        entity = lotMapper.update(entity, lotDTO);
        entity = lotService.update(entity);
        return ResponseEntity.ok(lotMapper.toDTO(entity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotDTO> findById(@PathVariable Long id) {
        Lot fetchedLot = lotService.getById(id);
        return ResponseEntity.ok(lotMapper.toDTO(fetchedLot));
    }

    @GetMapping
    public ResponseEntity<List<LotDTO>> findAll() {
        List<Lot> fetchedLotsList = lotService.getAll();
        return ResponseEntity.ok(lotMapper.toDTOs(fetchedLotsList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
