package com.api.starwars.consumers.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanetResponseDTO {

    private final Integer statusCode;
    private final PlanetResponseBodyDTO planetBodyDto;

    public PlanetResponseDTO(Integer statusCode, PlanetResponseBodyDTO planetBodyDto) {
        this.statusCode = statusCode;
        this.planetBodyDto = planetBodyDto;
    }

}
