package com.vention.agroex.controller;

import com.vention.agroex.dto.LotDTO;
import com.vention.agroex.service.LotService;
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

    @PostMapping()
    public ResponseEntity<LotDTO> save(@RequestBody LotDTO lotDTO) {
        var savedLot = lotService.save(lotDTO);
        return ResponseEntity.ok(savedLot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotDTO> update(@PathVariable Long id, @RequestBody LotDTO lot) {
        var updatedLot = lotService.update(id, lot);
        return ResponseEntity.ok(updatedLot);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotDTO> findById(@PathVariable Long id) {
        var fetchedLot = lotService.getById(id);
        return ResponseEntity.ok(fetchedLot);
    }

    @GetMapping()
    public ResponseEntity<List<LotDTO>> findAll() {
        var fetchedLotsList = lotService.getAll();
        return ResponseEntity.ok(fetchedLotsList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
