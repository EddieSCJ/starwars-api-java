package com.api.starwars.planets.storage.interfaces;

public interface ISQSManager {

    void sendDeleteMessage(String planetName);

}
