package com.api.starwars.planets.app.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

import static org.springframework.http.HttpMethod.*;

@AllArgsConstructor
@Getter
public enum OperationsEnum {
    GET_PLANETS_PAGE("get_planets_page", GET, Constants.PLANETS_ENDPOINT),
    GET_PLANET_BY_NAME("get_planet_by_name", GET, Constants.PLANETS_ENDPOINT + "/?search={name}"),
    GET_PLANET_BY_ID("get_planet_by_id", GET, Constants.PLANETS_ENDPOINT + "/{id}"),
    CREATE_PLANET("create_planet", POST, Constants.PLANETS_ENDPOINT),
    UPDATE_PLANET("update_planet", PUT, Constants.PLANETS_ENDPOINT),
    DELETE_PLANET("delete_planet", DELETE, Constants.PLANETS_ENDPOINT + "/{id}");

    private final String name;
    private final HttpMethod method;
    private final String path;

}