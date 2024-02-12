package com.vention.agroex.controller;

import com.vention.agroex.dto.Image;
import com.vention.agroex.dto.Lot;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.mapper.LotMapper;
import com.vention.agroex.util.validator.LotDTOValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lots")
@Tag(name = "Lot controller")
public class LotController {

    private final LotService lotService;
    private final LotMapper lotMapper;
    private final LotDTOValidator lotDTOValidator;

    @PostMapping
    public ResponseEntity<Lot> save(@RequestBody @Valid Lot lot,
                                    BindingResult bindingResult){
        lotDTOValidator.validate(lot, bindingResult);
        var saved = lotService.save(lotMapper.toEntity(lot));
        return ResponseEntity.ok(lotMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lot> update(@PathVariable Long id,
                                      @RequestBody @Valid Lot lot,
                                      BindingResult bindingResult) {
        lotDTOValidator.validate(lot, bindingResult);
        var saved = lotService.update(id, lotMapper.toEntity(lot));
        return ResponseEntity.ok(lotMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lot> findById(@PathVariable Long id) {
        var fetchedLotEntity = lotService.getById(id);
        return ResponseEntity.ok(lotMapper.toDTO(fetchedLotEntity));
    }

    @GetMapping()
    public ResponseEntity<List<Lot>> findAll() {
        var fetchedLotsList = lotService.getAll();
        return ResponseEntity.ok(lotMapper.toDTOs(fetchedLotsList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/images")
    public ResponseEntity<String> uploadImageForLot(@PathVariable("id") Long lotId,
                                  @RequestParam("file") MultipartFile file){
        var image = new Image(file);
        var saved = lotService.uploadImage(lotId, image);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}/images")
    @ResponseStatus(HttpStatus.OK)
    public void deleteImageForLot(@PathVariable("id") Long lotId,
                                  @RequestParam("fileName") String fileName){
        lotService.deleteImage(fileName);
    }
}
