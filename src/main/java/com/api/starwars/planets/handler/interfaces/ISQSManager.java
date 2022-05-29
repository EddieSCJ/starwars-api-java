package com.api.starwars.planets.handler.interfaces;

public interface ISQSManager {

    void sendDeleteMessage(String planetName);

}
