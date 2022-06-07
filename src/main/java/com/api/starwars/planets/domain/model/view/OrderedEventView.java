package com.api.starwars.planets.domain.model.view;

import com.api.starwars.planets.domain.model.event.OrderedEvent;
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
public class OrderedEventView {
    private final String key;
    private final EventView event;

    public static OrderedEventView fromDomain(OrderedEvent orderedEvent) {
        final EventView eventView = EventView.fromDomain(orderedEvent.event());
        return OrderedEventView.builder()
                .key(orderedEvent.key())
                .event(eventView)
                .build();
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
