package com.vention.agroex.service;

import com.vention.agroex.entity.Country;

import java.util.List;

public interface CountryService {

    List<Country> getAll();

    Country getById(Long id);

    Country save(Country country);

    void deleteById(Long id);

    Country update(Country country, Long id);

}
