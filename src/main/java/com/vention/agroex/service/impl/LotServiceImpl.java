package com.vention.agroex.service.impl;

import com.vention.agroex.entity.Lot;
import com.vention.agroex.exception.NoSuchLotException;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.LotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotRepository lotRepository;

    @Override
    public Lot save(Lot lot) {
        return lotRepository.save(lot);
    }

    @Override
    public Lot findById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchLotException("There is no such lot with id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        lotRepository.deleteById(id);
    }

    @Override
    public List<Lot> findAll() {
        return lotRepository.findAll();
    }

    @Override
    public Lot update(Long id, Lot lot) {
        return lotRepository.update(id, lot);
    }
}
