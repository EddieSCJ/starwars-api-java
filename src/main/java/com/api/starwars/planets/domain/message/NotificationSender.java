package com.api.starwars.planets.domain.message;

public interface NotificationSender {
    void sendNotification(String subject, String notification);
}
