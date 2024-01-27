package com.vention.agroex.controller;

import com.vention.agroex.entity.Lot;
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
    public ResponseEntity<Lot> save(@RequestBody Lot lot) {
        var savedLot = lotService.save(lot);
        return ResponseEntity.ok(savedLot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lot> update(@PathVariable Long id, @RequestBody Lot lot) {
        var updatedLot = lotService.update(id, lot);
        return ResponseEntity.ok(updatedLot);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lot> findById(@PathVariable Long id) {
        var fetchedLot = lotService.findById(id);
        return ResponseEntity.ok(fetchedLot);
    }

    @GetMapping()
    public ResponseEntity<List<Lot>> findAll() {
        var fetchedLotsList = lotService.findAll();
        return ResponseEntity.ok(fetchedLotsList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
