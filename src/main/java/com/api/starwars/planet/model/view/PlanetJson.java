package com.api.starwars.planet.model.view;

import com.api.starwars.planet.model.domain.Planet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.starwars.helpers.ErrorMessageHelper.notEmpty;
import static com.api.starwars.helpers.ErrorMessageHelper.notNull;

@Data
@AllArgsConstructor
@Builder
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
    private final Integer _quantidadeDeAparicoesEmFilmes;

    @NotNull(message = "O cache " + notNull)
    private final Long _cacheEmDias;

    public Planet toDomain() {
        return Planet.builder()
                .id(this.id)
                .name(this.nome)
                .climate(this.clima)
                .terrain(this.terreno)
                .movieAppeareces(this._quantidadeDeAparicoesEmFilmes)
                .cacheInDays(_cacheEmDias)
                .build();
    }

    public static PlanetJson fromDomain(Planet planet) {
        return PlanetJson.builder()
                .id(planet.getId())
                .nome(planet.getName())
                .clima(planet.getClimate())
                .terreno(planet.getTerrain())
                ._quantidadeDeAparicoesEmFilmes(planet.getMovieAppeareces())
                ._cacheEmDias(planet.getCacheInDays())
                .build();
    }

}
