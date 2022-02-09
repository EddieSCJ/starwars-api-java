package com.api.starwars.planet.model.view;

import com.api.starwars.planet.model.domain.Planet;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.commons.helpers.ErrorMessageHelper.notEmpty;
import static com.api.commons.helpers.ErrorMessageHelper.notNull;

public record PlanetJson(String id,
                         @NotEmpty(message = "O nome " + notEmpty) @NotNull(message = "O nome " + notNull) String nome,
                         @NotEmpty(message = "O clima " + notEmpty) @NotNull(message = "O clima " + notNull) String clima,
                         @NotEmpty(message = "O terreno " + notEmpty) @NotNull(message = "O terreno " + notNull) String terreno,
                         @NotNull(message = "O cache " + notNull) Long _cacheEmDias,
                         Integer _quantidadeDeAparicoesEmFilmes
) {

    public static PlanetJson fromDomain(Planet planet) {
        return new PlanetJson(
                planet.id(),
                planet.name(),
                planet.climate(),
                planet.terrain(),
                planet.cacheInDays(),
                planet.movieAppearances()
        );

    }

    public Planet toDomain() {
        return new Planet(
                this.id(),
                this.nome(),
                this.terreno(),
                this.clima(),
                this._quantidadeDeAparicoesEmFilmes(),
                this._cacheEmDias()
        );
    }

}
