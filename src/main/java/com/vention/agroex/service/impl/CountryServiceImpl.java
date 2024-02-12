package com.vention.agroex.service.impl;

import com.vention.agroex.entity.CountryEntity;
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
    public List<CountryEntity> getAll() {
        return countryRepository.findAll();
    }

    @Override
    public CountryEntity getById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country with this id not found!"));
    }

    @Override
    public CountryEntity save(CountryEntity countryEntity) {
        return countryRepository.save(countryEntity);
    }

    @Override
    public void deleteById(Long id) {
        var countryEntity = getById(id);
        countryRepository.delete(countryEntity);
    }

    @Override
    public CountryEntity update(Long id, CountryEntity countryEntity) {
        getById(id);
        countryEntity.setId(id);
        return countryRepository.save(countryEntity);
    }
}
