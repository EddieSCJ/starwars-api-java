package com.api.starwars.planet.model.mongo;

import com.api.starwars.planet.model.domain.Planet;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.transaction.annotation.Transactional;

@Document(collection = "planets")
@Data
@Builder
public class MongoPlanet {

    @Id
    public String id;

    @Indexed(unique = true)
    private final String name;
    private final String climate;
    private final String terrain;

    public MongoPlanet(String name, String climate, String terrain) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

    public MongoPlanet(Planet planet) {
        this.name = planet.getName();
        this.climate = planet.getClimate();
        this.terrain = planet.getTerrain();
    }

    public Planet toDomain() {
        return Planet.builder()
                .id(this.id)
                .name(this.name)
                .terrain(this.terrain)
                .climate(this.climate)
                .build();
    }

}
