package com.vention.agroex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.exception.JsonIOException;
import com.vention.agroex.model.SSEBetsResponse;
import com.vention.agroex.model.SSEBetsSubscription;
import com.vention.agroex.model.SubscriptionGreeting;
import com.vention.agroex.service.LotService;
import com.vention.agroex.util.mapper.BetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@EnableScheduling
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SSEBetsController {

    private final ObjectMapper objectMapper;
    private final BetMapper betMapper;
    private final LotService lotService;

    Map<UUID, SSEBetsSubscription> subscriptions = new ConcurrentHashMap<>();

    @GetMapping(path = "/open-sse-bets-stream/{lotId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> openSseStream(@PathVariable Long lotId) {
        var lot = lotService.getById(lotId);
        var subId = UUID.randomUUID();

        return Flux.create(fluxSink -> {
            log.info("create subscription with id " + subId);
            fluxSink.onCancel(() -> {
                subscriptions.remove(subId);
                log.info("subscription " + subId + " was closed");
            });

            subscriptions.put(subId, new SSEBetsSubscription(lotId, fluxSink));
            var bets = lot.getBets();
            if (!bets.isEmpty()) {
                bets.forEach(bet -> sendBet(lotId, bet, lot.getStatus()));
            } else {
                ServerSentEvent<SubscriptionGreeting> helloEvent = ServerSentEvent.builder(new SubscriptionGreeting("Connected successfully")).build();
                fluxSink.next(helloEvent);
            }
        });
    }

    public void sendBet(Long lotId, BetEntity betEntity, String lotStatus) {
        var lot = lotService.getById(lotId);
        subscriptions.values().forEach((subscription -> {
            ServerSentEvent<String> event;
            try {
                event = ServerSentEvent
                        .builder(objectMapper.writeValueAsString(new SSEBetsResponse(betMapper.toDTO(betEntity), lotStatus)))
                        .build();
            } catch (JsonProcessingException e) {
                throw new JsonIOException("Error while bet sending");
            }
            if (subscription.lotId().equals(lotId)) {
                subscription.link().next(event);
            }
        }));
    }
}