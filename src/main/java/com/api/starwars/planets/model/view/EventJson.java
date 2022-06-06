package com.api.starwars.planets.model.view;

import com.api.starwars.planets.model.domain.Event;
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
public class EventJson {
    private final String type;
    private final String event;
    private final String message;

    public static EventJson fromDomain(Event event) {
        return EventJson.builder()
                .type(event.type())
                .event(event.event())
                .message(event.planetName())
                .build();
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
}
