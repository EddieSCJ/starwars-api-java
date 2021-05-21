package com.api.starwars.domain.dtos;

import com.api.starwars.domain.Planet;
import lombok.Data;

@Data
public class PlanetDTO {

    private final String id;
    private final String nome;
    private final String clima;
    private final String terreno;
    private final Integer quantidadeDeAparicoesEmFilmes;

    public PlanetDTO(Planet planet, Integer appearances) {
        this.id = planet.getId();
        this.nome = planet.getName();
        this.clima = planet.getClimate();
        this.terreno = planet.getTerrain();
        this.quantidadeDeAparicoesEmFilmes = appearances;
    }

}
