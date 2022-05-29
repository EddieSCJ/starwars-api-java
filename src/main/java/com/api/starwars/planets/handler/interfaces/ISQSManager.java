package com.api.starwars.planets.handler.interfaces;

public interface ISQSManager {

    String sendDeleteEvent(String planetName);

}
