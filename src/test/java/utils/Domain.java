package utils;

import com.api.starwars.domain.planets.model.domain.Planet;
import com.api.starwars.domain.planets.model.mongo.MongoPlanet;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Domain {

    private static Faker faker = new Faker();
    private static Random random = new Random();

    public static Planet getRandomPlanet() {
        String[] weathers = new String[]{faker.weather().description(), faker.weather().description()};
        String[] terrain = new String[]{faker.country().name(), faker.country().name()};
        return new Planet(
                faker.idNumber().toString(),
                faker.pokemon().name(),
                weathers,
                terrain,
                random.nextInt(),
                random.nextLong()
        );
    }

    public static List<Planet> getRandomPlanetList() {
        List<Planet> planets = new ArrayList<>();
        for (int i = 0; i<3; i++) {
            planets.add(getRandomPlanet());
        }

        return planets;
    }

    public static MongoPlanet getRandomMongoPlanet() {
        return MongoPlanet.fromDomain(getRandomPlanet());
    }

    public static List<MongoPlanet> getRandomMongoPlanetList() {
        List<MongoPlanet> mongoPlanets = new ArrayList<>();
        for (int i = 0; i<3; i++) {
            mongoPlanets.add(getRandomMongoPlanet());
        }

        return mongoPlanets;
    }
}
