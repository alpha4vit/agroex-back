package com.vention.agroex.service.impl;

import com.vention.agroex.dto.LotDTO;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.mapper.LotMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;
    private final LotMapper lotMapper;

    @Override
    public LotDTO save(LotDTO lotDTO) {
        return lotMapper.toDTO(lotRepository.save(lotMapper.toEntity(lotDTO)));
    }

    @Override
    public LotDTO getById(Long id) {
        return lotMapper.toDTO(lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no lots with id %d", id))));
    }

    @Override
    public void deleteById(Long id) {
        lotRepository.deleteById(id);
    }

    @Override
    public List<LotDTO> getAll() {
        return lotRepository.findAll()
                .stream()
                .map(lotMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LotDTO update(Long id, LotDTO lotDTO) {
        var lot = lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("There is no lots with id %d", id)));
        lotMapper.update(lot, lotDTO);
        return lotMapper.toDTO(lotRepository.save(lot));
    }
}
