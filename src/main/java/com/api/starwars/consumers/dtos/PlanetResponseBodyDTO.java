package com.api.starwars.consumers.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlanetResponseBodyDTO {

    private final Integer count;
    private final List<PlanetDTO> results;


}
