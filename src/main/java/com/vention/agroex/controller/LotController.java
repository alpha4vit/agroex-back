package com.vention.agroex.controller;

import com.vention.agroex.dto.Lot;
import com.vention.agroex.model.LotRejectRequest;
import com.vention.agroex.model.LotStatusResponse;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.mapper.LotMapper;
import com.vention.agroex.util.validator.LotValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final LotValidator lotValidator;

    @PostMapping
    public ResponseEntity<Lot> save(@RequestPart(value = "file", required = false) MultipartFile[] files,
                                    @RequestHeader("currency") String currency,
                                    @RequestPart("data") @Valid Lot lot,
                                    BindingResult bindingResult) {
        lotValidator.validate(lot, bindingResult);
        var saved = lotService.save(lotMapper.toEntity(lot), files, currency);
        return ResponseEntity.ok(lotMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.isLotOwner(#id)")
    public ResponseEntity<Lot> update(@PathVariable("id") Long id,
                                      @RequestHeader("currency") String currency,
                                      @RequestPart(value = "file", required = false) MultipartFile[] files,
                                      @RequestPart("data") @Valid Lot lot,
                                      BindingResult bindingResult) {
        lotValidator.validate(lot, bindingResult);
        var saved = lotService.update(id, lotMapper.toEntity(lot), files, currency);
        return ResponseEntity.ok(lotMapper.toDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lot> findById(@PathVariable Long id,
                                        @RequestHeader("currency") String currency) {
        var fetchedLotEntity = lotService.getById(id, currency);
        return ResponseEntity.ok(lotMapper.toDTO(fetchedLotEntity));
    }

    @GetMapping()
    public ResponseEntity<List<Lot>> search(@RequestParam Map<String, String> filters,
                                            @RequestHeader("currency") String currency,
                                            @RequestParam(defaultValue = "0") int pageNumber,
                                            @RequestParam(defaultValue = "50") int pageSize) {
        var lots = lotService.getWithCriteria(filters, pageNumber, pageSize, currency);
        return ResponseEntity.ok(lotMapper.toDTOs(lots));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.isLotOwner(#id)")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        lotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<LotStatusResponse> getLotStatus(@PathVariable Long id) {
        return ResponseEntity.ok(lotService.getLotStatus(id));
    }

    @PostMapping("/{id}/userStatus")
    public ResponseEntity<Lot> putOnModeration(@PathVariable("id") Long lotId, @RequestParam boolean status) {
        var moderated = lotService.changeUserStatus(lotId, status);
        return ResponseEntity.ok(lotMapper.toDTO(moderated));
    }

    @PostMapping("/{id}/moderate")
    public ResponseEntity<Lot> putOnModeration(@PathVariable("id") Long lotId,
                                               @RequestHeader("currency") String currency) {
        var moderated = lotService.putOnModeration(lotId, currency);
        return ResponseEntity.ok(lotMapper.toDTO(moderated));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Lot> approve(@PathVariable("id") Long lotId,
                                       @RequestHeader("currency") String currency) {
        var approved = lotService.approve(lotId, currency);
        return ResponseEntity.ok(lotMapper.toDTO(approved));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Lot> reject(@RequestBody LotRejectRequest rejectRequest) {
        var rejected = lotService.reject(rejectRequest);
        return ResponseEntity.ok(lotMapper.toDTO(rejected));
    }
}
