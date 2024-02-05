package com.vention.agroex.controller;

import com.vention.agroex.dto.CountryDTO;
import com.vention.agroex.entity.Country;
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
    public ResponseEntity<List<CountryDTO>> getAll(){
        return ResponseEntity.ok(countryMapper.toDtos(countryService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CountryDTO> getById(@PathVariable("id") Long id){
        var user = countryService.getById(id);
        return ResponseEntity.ok(countryMapper.toDTO(user));
    }

    @PostMapping
    public ResponseEntity<CountryDTO> createUser(@RequestBody @Valid CountryDTO countryDTO,
                                                 BindingResult bindingResult){
        countryDTOValidator.validate(countryDTO, bindingResult);
        Country entity = countryMapper.toEntity(countryDTO);
        entity = countryService.save(entity);
        return ResponseEntity.ok(countryMapper.toDTO(entity));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUserById(@PathVariable("id") Long id){
        countryService.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CountryDTO> updateUser(@PathVariable("id") Long id,
                                              @RequestBody @Valid CountryDTO countryDTO,
                                                 BindingResult bindingResult){
        countryDTOValidator.validate(countryDTO, bindingResult);
        Country entity = countryMapper.toEntity(countryDTO);
        entity = countryService.update(entity, id);
        return ResponseEntity.ok(countryMapper.toDTO(entity));
    }
}
