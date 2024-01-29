package com.vention.agroex.controller;

import com.vention.agroex.dto.LotDTO;
import com.vention.agroex.dto.ImageDTO;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.mapper.LotMapper;
import com.vention.agroex.util.validator.LotDTOValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/lots")
@RequiredArgsConstructor
@Tag(name = "Lot controller")
public class LotController {

    private final LotService lotService;
    private final LotMapper lotMapper;
    private final LotDTOValidator lotDTOValidator;

    @PostMapping
    public ResponseEntity<LotDTO> save(@RequestBody @Valid LotDTO lotDTO,
                                       BindingResult bindingResult) {
        lotDTOValidator.validate(lotDTO, bindingResult);
        Lot entity = lotMapper.toEntity(lotDTO);
        entity = lotService.save(entity);
        return ResponseEntity.ok(lotMapper.toDTO(entity));
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<LotDTO> update(@PathVariable Long id,
                                         @RequestBody @Valid LotDTO lotDTO,
                                         BindingResult bindingResult) {
        lotDTOValidator.validate(lotDTO, bindingResult);
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

    @GetMapping()
    public ResponseEntity<List<LotDTO>> findAll() {
        List<Lot> fetchedLotsList = lotService.getAll();
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
        ImageDTO image = new ImageDTO(file);
        String saved = lotService.uploadImage(lotId, image);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}/images")
    @ResponseStatus(HttpStatus.OK)
    public void deleteImageForLot(@PathVariable("id") Long lotId,
                                  @RequestParam("fileName") String fileName){
        lotService.deleteImage(fileName);
    }
}
