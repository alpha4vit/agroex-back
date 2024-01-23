package com.vention.agroex.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vention.agroex.entity.Lot;
import com.vention.agroex.exception.LotListInitializationException;
import com.vention.agroex.exception.LotListSavingException;
import com.vention.agroex.exception.NoSuchLotException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Repository
public class LotRepository {

    public Map<Long, Lot> lotsInMemory = new HashMap<>();
    private Long idCounter = 0L;
    ObjectMapper objectMapper = new ObjectMapper();
    File lotsDbFile = new File("src/main/resources/lot_db.json");

    @PostConstruct
    private void init() {
        try {
            objectMapper.findAndRegisterModules();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            log.info("Reading lots array from JSON file");
            List<Lot> lotsList = Arrays.stream(objectMapper.readValue(lotsDbFile, Lot[].class)).toList();
            lotsList.forEach(lot -> lotsInMemory.put(lot.getId(), lot));
            idCounter = lotsInMemory
                    .keySet()
                    .stream()
                    .max(Long::compareTo)
                    .orElse(0L);
            log.info("Lots map uploaded from JSON file");
        } catch (IOException e) {
            throw new LotListInitializationException("Error while lots list initialization");
        }
    }

    private void saveToJSON() {
        List<Lot> lotsList = new ArrayList<>(lotsInMemory.values());
        try {
            objectMapper.writeValue(lotsDbFile, lotsList);
            log.info("Lots list saved to JSON file");
        } catch (IOException e) {
            throw new LotListSavingException("Error while saving list to JSON file");
        }
    }

    public Lot save(Lot lot) {
        idCounter++;
        lot.setId(idCounter);
        lotsInMemory.put(idCounter, lot);
        saveToJSON();
        return lot;
    }

    public Lot update(Long id, Lot lotToUpdate) {
        if (lotsInMemory.containsKey(id)) {
            lotToUpdate.setId(id);
            lotsInMemory.replace(id, lotToUpdate);
            log.info("Lot with id " + id + " updated");
        } else {
            log.error("There is no lots with id " + id);
            throw new NoSuchLotException("There is no lots with id " + id);
        }
        saveToJSON();
        return lotToUpdate;
    }

    public void deleteById(Long id) {
        lotsInMemory.remove(id);
        saveToJSON();
    }

    public Optional<Lot> findById(Long id) {
        return Optional.ofNullable(lotsInMemory.get(id));
    }

    public List<Lot> findAll() {
        return lotsInMemory.values().stream().toList();
    }
}