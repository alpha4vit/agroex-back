package com.vention.agroex.controller;

import com.vention.agroex.entity.CurrencyRateEntity;
import com.vention.agroex.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
public class CurrencyRateController {

    private final CurrencyRateService currencyRateService;

    @GetMapping
    public ResponseEntity<List<CurrencyRateEntity>> getAll(){
        return ResponseEntity.ok(currencyRateService.getAll());
    }

    @PostMapping
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public ResponseEntity<CurrencyRateEntity> create(@RequestBody CurrencyRateEntity rate){
        return ResponseEntity.ok(currencyRateService.create(rate));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public void deleteById(@PathVariable("id") Long id){
        currencyRateService.deleteById(id);
    }
}
