package com.api.starwars.planets.domain.model.view;

import com.api.starwars.planets.domain.model.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class EventView {
    private final String type;
    private final String event;
    private final String message;

    public static EventView fromDomain(Event event) {
        return EventView.builder()
                .type(event.type())
                .event(event.event())
                .message(event.planetName())
                .build();
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
