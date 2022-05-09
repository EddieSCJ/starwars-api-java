package com.api.starwars.domain.planets.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import static com.api.starwars.domain.planets.handlers.Constants.PLANETS_ENDPOINT;
import static org.springframework.http.HttpMethod.*;

@AllArgsConstructor
@Getter
public enum OperationsEnum {
    GET_PLANETS_PAGE("get_planets_page", GET, PLANETS_ENDPOINT),
    GET_PLANET_BY_NAME("get_planet_by_name", GET, PLANETS_ENDPOINT + "/?search={name}"),
    GET_PLANET_BY_ID("get_planet_by_id", GET, PLANETS_ENDPOINT + "/{id}"),
    CREATE_PLANET("create_planet", POST, PLANETS_ENDPOINT),
    UPDATE_PLANET("update_planet", PUT, PLANETS_ENDPOINT),
    DELETE_PLANET("delete_planet", DELETE,  PLANETS_ENDPOINT + "/{id}");

    private final String name;
    private final HttpMethod method;
    private final String path;

}