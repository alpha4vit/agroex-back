package com.vention.agroex.service.impl;

import com.vention.agroex.entity.Country;
import com.vention.agroex.repository.CountryRepository;
import com.vention.agroex.service.CountryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public List<Country> getAll() {
        return countryRepository.findAll();
    }

    @Override
    public Country getById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country with this id not found!"));
    }

    @Override
    public Country save(Country country) {
        return countryRepository.save(country);
    }

    @Override
    public void deleteById(Long id) {
        Country country = getById(id);
        countryRepository.delete(country);
    }

    @Override
    public Country update(Country country, Long id) {
        getById(id);
        country.setId(id);
        return countryRepository.save(country);
    }
}
