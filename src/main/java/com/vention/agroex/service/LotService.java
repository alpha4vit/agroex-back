package com.vention.agroex.service;

import com.vention.agroex.entity.Lot;
import org.springframework.stereotype.Service;
import com.vention.agroex.dto.ImageDTO;

import java.util.List;

@Service
public interface LotService {
    Lot save(Lot lot);

    Lot getById(Long id);

    void deleteById(Long id);

    List<Lot> getAll();

    Lot update(Lot lot);

}
