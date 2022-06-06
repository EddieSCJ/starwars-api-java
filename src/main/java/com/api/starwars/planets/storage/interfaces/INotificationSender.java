package com.api.starwars.planets.storage.interfaces;

public interface INotificationSender {
    void sendNotification(String subject, String notification);
}
