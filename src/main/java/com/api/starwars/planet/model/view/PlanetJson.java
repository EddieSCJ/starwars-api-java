package com.api.starwars.planet.model.view;

import com.api.starwars.planet.model.domain.Planet;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.commons.helpers.ErrorMessageHelper.notEmpty;
import static com.api.commons.helpers.ErrorMessageHelper.notNull;

@Data
public class PlanetJson {
    private final String id;

    @NotEmpty(message = "O nome " + notEmpty)
    @NotNull(message = "O nome " + notNull)
    private final String nome;

    @NotEmpty(message = "O clima " + notEmpty)
    @NotNull(message = "O clima " + notNull)
    private final String clima;

    @NotEmpty(message = "O terreno " + notEmpty)
    @NotNull(message = "O terreno " + notNull)
    private final String terreno;

    @NotNull(message = "O cache " + notNull)
    private final Long _cacheEmDias;
    private final Integer _quantidadeDeAparicoesEmFilmes;

    public PlanetJson(String id, String nome, String clima, String terreno, Long _cacheEmDias, Integer _quantidadeDeAparicoesEmFilmes) {
        this.id = id;
        this.nome = nome;
        this.clima = clima;
        this.terreno = terreno;
        this._cacheEmDias = _cacheEmDias;
        this._quantidadeDeAparicoesEmFilmes = _quantidadeDeAparicoesEmFilmes;
    }

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
                this.id,
                this.nome,
                this.clima,
                this.terreno,
                this._quantidadeDeAparicoesEmFilmes,
                this._cacheEmDias
        );
    }

}
