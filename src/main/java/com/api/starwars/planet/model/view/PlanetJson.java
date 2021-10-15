package com.api.starwars.planet.model.view;

import com.api.starwars.planet.model.domain.Planet;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.starwars.helpers.ErrorMessageHelper.notEmpty;
import static com.api.starwars.helpers.ErrorMessageHelper.notNull;

@Data
public class PlanetJson {

    private final String id;

    @NotEmpty(message = "O nome " + notEmpty)
    @NotNull(message = "O nome " + notNull)
    private final String nome;

    @NotEmpty(message = "O clima " + notEmpty)
    @NotNull(message = "O clima " + notNull)
    private final String clima;
    private final String terreno;

    @NotEmpty(message = "O terreno " + notEmpty)
    @NotNull(message = "O terreno " + notNull)
    private final Integer quantidadeDeAparicoesEmFilmes;

    public PlanetJson(Planet planet, Integer appearances) {
        this.id = planet.getId();
        this.nome = planet.getName();
        this.clima = planet.getClimate();
        this.terreno = planet.getTerrain();
        this.quantidadeDeAparicoesEmFilmes = appearances;
    }

}
