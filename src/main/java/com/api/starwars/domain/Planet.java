package com.api.starwars.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.api.starwars.helpers.ErrorMessageHelper.notEmpty;
import static com.api.starwars.helpers.ErrorMessageHelper.notNull;

@Document(collection = "planets")
@Data
public class Planet {

    @Id
    public String id;

    @NotEmpty(message = "O nome " + notEmpty)
    @NotNull(message = "O nome " + notNull)
    @JsonProperty("nome")
    @Indexed(unique = true)
    private final String name;

    @NotEmpty(message = "O clima " + notEmpty)
    @NotNull(message = "O clima " + notNull)
    @JsonProperty("clima")
    private final String climate;

    @NotEmpty(message = "O terreno " + notEmpty)
    @NotNull(message = "O terreno " + notNull)
    @JsonProperty("terreno")
    private final String terrain;

    public Planet(String name, String climate, String terrain) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

}
