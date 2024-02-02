package com.vention.agroex.service;

import com.vention.agroex.dto.LotDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LotService {
    LotDTO save(LotDTO lotDTO);

    LotDTO getById(Long id);

    void deleteById(Long id);

    List<LotDTO> getAll();

    LotDTO update(Long id, LotDTO lot);
}
