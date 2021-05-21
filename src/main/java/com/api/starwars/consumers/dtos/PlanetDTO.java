package com.api.starwars.consumers.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanetDTO {
    private final List<String> films;
}
