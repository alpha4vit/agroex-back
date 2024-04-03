package com.vention.agroex.model;

import com.vention.agroex.util.constant.Role;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

import java.util.Set;
import java.util.UUID;


public record NotificationSubscriptionData(
        UUID userId, FluxSink<ServerSentEvent> fluxSink, Set<Role> roles) {
}
