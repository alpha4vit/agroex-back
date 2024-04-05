package com.vention.agroex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vention.agroex.exception.JsonIOException;
import com.vention.agroex.model.NotificationSubscriptionData;
import com.vention.agroex.model.SubscriptionGreeting;
import com.vention.agroex.service.NotificationService;
import com.vention.agroex.service.UserService;
import com.vention.agroex.util.constant.NotificationReadStatusConstants;
import com.vention.agroex.util.constant.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@EnableScheduling
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SSENotificationController {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    Map<UUID, NotificationSubscriptionData> subscriptions = new ConcurrentHashMap<>();

    @GetMapping(path = "/open-sse-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent> openSseStream() {
        var user = userService.getAuthenticatedUser();
        var userId = user.getId();
        var roles = user.getRoles();

        return Flux.create(fluxSink -> {
            log.info("create subscription for " + userId);
            fluxSink.onCancel(() -> {
                subscriptions.remove(userId);
                log.info("subscription " + userId + " was closed");
            });
            ServerSentEvent<SubscriptionGreeting> helloEvent = ServerSentEvent.builder(new SubscriptionGreeting("Connected successfully")).build();
            fluxSink.next(helloEvent);
            subscriptions.put(userId, new NotificationSubscriptionData(userId, fluxSink, roles));
        });
    }

    @PostMapping(path = "/markRead/{notificationId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void markAsRead(@PathVariable UUID notificationId) {
        var notification = notificationService.getById(notificationId);
        notification.setReadStatus(NotificationReadStatusConstants.READ);
        notificationService.save(notification);
    }

    @Scheduled(fixedRate = 6000)
    public void sendUnread() {

        var notifications = notificationService.getAll();

        notifications.stream().filter(notificationEntity ->
                        notificationEntity.getReadStatus().equals(NotificationReadStatusConstants.UNREAD))
                .forEach(notificationEntity -> {
                    ServerSentEvent<String> event;
                    try {
                        event = ServerSentEvent
                                .builder(objectMapper.writeValueAsString(notificationEntity))
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new JsonIOException("Error while notification sending");
                    }
                    if (notificationEntity.getRole().equals(Role.ADMIN)) {
                        subscriptions.forEach(((uuid, subscription) -> {
                            if (subscription.roles().contains(notificationEntity.getRole())) {
                                subscription.fluxSink().next(event);
                            }
                        }));
                    } else {
                        subscriptions.forEach((uuid, subscription) -> {
                            if (subscription.userId().equals(notificationEntity.getUserId())) {
                                subscription.fluxSink().next(event);
                            }
                        });
                    }
                });
    }
}