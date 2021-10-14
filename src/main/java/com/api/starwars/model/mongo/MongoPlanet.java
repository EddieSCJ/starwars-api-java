package com.api.starwars.model.mongo;

import com.api.starwars.model.domain.Planet;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "planets")
@Data
@Builder
public class MongoPlanet {

    public static String FIELD_ID = "_id";
    public static String FIELD_NAME = "name";
    public static String FIELD_CLIMATE = "climate";
    public static String FIELD_TERRAIN = "terrain";

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
