package com.vention.agroex.service;

import com.vention.agroex.entity.CountryEntity;

import java.util.List;

public interface CountryService {

    List<CountryEntity> getAll(Boolean lotExisted);

    CountryEntity getById(Long id);

    CountryEntity save(CountryEntity countryEntity);

    void deleteById(Long id);

    CountryEntity update(Long id, CountryEntity countryEntity);

}
