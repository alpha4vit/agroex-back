package com.vention.agroex.controller;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.model.LotStatusResponse;
import com.vention.agroex.service.CurrencyRateService;
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
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lots")
@Tag(name = "Lot controller")
public class LotController {

    private final LotService lotService;
    private final LotMapper lotMapper;
    private final LotDTOValidator lotDTOValidator;
    private final CurrencyRateService currencyRateService;

    @PostMapping
    public ResponseEntity<Lot> save(@RequestPart(value = "file", required = false) MultipartFile[] files,
                                    @RequestHeader("currency") String currency,
                                    @RequestPart("data") @Valid Lot lot,
                                    BindingResult bindingResult) {
        lotDTOValidator.validate(lot, bindingResult);
        var saved = lotService.save(lotMapper.toEntity(lot), files);
        var currencies = currencyRateService.getAll();
        saved.updatePrice(currency, currencies);
        return ResponseEntity.ok(lotMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lot> update(@PathVariable("id") Long id,
                                      @RequestHeader("currency") String currency,
                                      @RequestPart(value = "file", required = false) MultipartFile[] files,
                                      @RequestPart("data") @Valid Lot lot,
                                      BindingResult bindingResult) {
        lotDTOValidator.validate(lot, bindingResult);
        var saved = lotService.update(id, lotMapper.toEntity(lot), files);
        var currencies = currencyRateService.getAll();
        saved.updatePrice(currency, currencies);
        return ResponseEntity.ok(lotMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lot> findById(@PathVariable Long id,
                                        @RequestHeader("currency") String currency) {
        var fetchedLotEntity = lotService.getById(id);
        var currencies = currencyRateService.getAll();
        fetchedLotEntity.updatePrice(currency, currencies);
        return ResponseEntity.ok(lotMapper.toDTO(fetchedLotEntity));
    }

    @GetMapping()
    public ResponseEntity<List<Lot>> search(@RequestParam Map<String, String> filters,
                            @RequestHeader("currency") String currency,
                            @RequestParam(defaultValue = "0") int pageNumber,
                            @RequestParam(defaultValue = "50") int pageSize) {
        var lots = lotService.getWithCriteria(filters, pageNumber, pageSize);
        var currencies = currencyRateService.getAll();
        lots.forEach(lot -> lot.updatePrice(currency, currencies));
        return ResponseEntity.ok(lotMapper.toDTOs(lots));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/images")
    @ResponseStatus(HttpStatus.OK)
    public void deleteImageForLot(@PathVariable("id") Long lotId,
                                  @RequestParam("fileName") String fileName) {
        lotService.deleteImage(fileName);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<LotStatusResponse> getLotStatus(@PathVariable Long id) {
        return ResponseEntity.ok(lotService.getLotStatus(id));
    }

    @PostMapping("/{id}/moderate")
    public ResponseEntity<Lot> putOnModeration(@PathVariable("id") Long lotId) {
        var moderated = lotService.putOnModeration(lotId);
        return ResponseEntity.ok(lotMapper.toDTO(moderated));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Lot> approve(@PathVariable("id") Long lotId) {
        var approved = lotService.approve(lotId);
        return ResponseEntity.ok(lotMapper.toDTO(approved));
    }
}
