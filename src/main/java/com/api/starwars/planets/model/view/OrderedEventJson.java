package com.api.starwars.planets.model.view;

import com.api.starwars.planets.model.domain.OrderedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Ordered Events are made in case we need maintain sending message order
 * Once we need it, the key parameter is the key of the process to maintain
 * Because it make all messages go to same partition
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class OrderedEventJson {
    private final String key;
    private final EventJson event;

    public static OrderedEventJson fromDomain(OrderedEvent orderedEvent) {
        final EventJson eventJson = EventJson.fromDomain(orderedEvent.event());
        return OrderedEventJson.builder()
                .key(orderedEvent.key())
                .event(eventJson)
                .build();
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
