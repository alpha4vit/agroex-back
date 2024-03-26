package com.vention.agroex.controller;

import com.vention.agroex.dto.Location;
import com.vention.agroex.repository.LocationRepository;
import com.vention.agroex.util.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @GetMapping
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public ResponseEntity<List<Location>> findAll(){
        return ResponseEntity.ok(locationMapper.toDTOs(locationRepository.findAll()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSecurityExpression.isAdmin()")
    public void deleteById(@PathVariable("id") Long id){
        locationRepository.deleteById(id);
    }
}
