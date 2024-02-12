package com.vention.agroex.controller;

import com.vention.agroex.dto.Country;
import com.vention.agroex.service.CountryService;
import com.vention.agroex.util.mapper.CountryMapper;
import com.vention.agroex.util.validator.CountryDTOValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/countries")
@Tag(name = "Country controller")
public class CountryController {
    
    private final CountryService countryService;
    private final CountryMapper countryMapper;
    private final CountryDTOValidator countryDTOValidator;

    @GetMapping
    public ResponseEntity<List<Country>> getAll(){
        return ResponseEntity.ok(countryMapper.toDtos(countryService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getById(@PathVariable("id") Long id){
        var countryEntity = countryService.getById(id);
        return ResponseEntity.ok(countryMapper.toDTO(countryEntity));
    }

    @PostMapping
    public ResponseEntity<Country> createCountry(@RequestBody @Valid Country country,
                                              BindingResult bindingResult){
        countryDTOValidator.validate(country, bindingResult);
        var saved = countryService.save(countryMapper.toEntity(country));
        return ResponseEntity.ok(countryMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCountryById(@PathVariable("id") Long id){
        countryService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable("id") Long id,
                                              @RequestBody @Valid Country country,
                                              BindingResult bindingResult){
        countryDTOValidator.validate(country, bindingResult);
        var saved = countryService.update(id, countryMapper.toEntity(country));
        return ResponseEntity.ok(countryMapper.toDTO(saved));
    }
}
