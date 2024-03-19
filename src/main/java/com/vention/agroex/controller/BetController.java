package com.vention.agroex.controller;

import com.vention.agroex.dto.Bet;
import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.model.BetRequest;
import com.vention.agroex.service.BetService;
import com.vention.agroex.service.LotService;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.mapper.BetMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bets")
@Tag(name = "Bets controller")
public class BetController {

    private final BetMapper betMapper;
    private final BetService betService;
    private final UserService userService;
    private final LotService lotService;

    @PostMapping("/{id}")
    public ResponseEntity<Bet> makeBet(@PathVariable("id") Long lotId,
                                       @RequestBody BetRequest betRequest,
                                       @RequestHeader("currency") String currency) {
        var bet = betMapper.requestToEntity(betRequest);
        var user = userService.getById(betRequest.userId());
        var lot = lotService.getById(lotId, currency);

        bet.setUser(user);
        bet.setLot(lot);

        return ResponseEntity.ok(betMapper.toDTO(betService.makeBet(lotId, bet, currency)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Bet>> getBetsList(@PathVariable("id") Long lotId) {
        var bets = betService.getLotBets(lotId);
        bets.sort(Comparator.comparing(BetEntity::getBetTime));
        return ResponseEntity.ok(betMapper.toDTOs(bets));
    }
}
