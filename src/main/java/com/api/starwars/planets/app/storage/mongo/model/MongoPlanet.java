package com.api.starwars.planets.app.storage.mongo.model;

import com.api.starwars.planets.domain.model.Planet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Duration;
import java.time.LocalDateTime;

@Document(collection = "planets")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class MongoPlanet {

    @Id
    @Field("_id")
    @Indexed(unique = true)
    private final ObjectId id;

    @Indexed(unique = true)
    private String name;
    private final String[] climate;
    private final String[] terrain;
    private final Integer movieAppearances;

    @Field("created")
    private LocalDateTime creationDate;

    public static MongoPlanet fromDomain(Planet planet) {
        ObjectId id = planet.id() != null ? new ObjectId(planet.id()) : null;
        return new MongoPlanet(
                id,
                planet.name(),
                planet.climate(),
                planet.terrain(),
                planet.movieAppearances(),
                LocalDateTime.now()
        );
    }

    public Planet toDomain() {
        LocalDateTime now = LocalDateTime.now();
        if (creationDate == null) {
            creationDate = now;
        }

        long daysBetween = Duration.between(creationDate, now).toDays();

        String id = this.id != null ? this.id.toString() : null;
        return new Planet(
                id,
                this.name,
                this.climate,
                this.terrain,
                this.movieAppearances,
                daysBetween
        );
    }

}
