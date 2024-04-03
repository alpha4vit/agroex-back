package com.vention.agroex.model;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

public record SSEBetsSubscription(Long lotId, FluxSink<ServerSentEvent> link) {
}
