package com.api.starwars.planets.aws.sns;

import com.api.starwars.planets.storage.interfaces.ISNSManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.stereotype.Service;

import static java.text.MessageFormat.format;

@Slf4j
@Service
public class SNSManager implements ISNSManager {

    private final NotificationMessagingTemplate notificationMessagingTemplate;

    @Autowired
    public SNSManager(NotificationMessagingTemplate notificationMessagingTemplate) {
        this.notificationMessagingTemplate = notificationMessagingTemplate;
    }

    public void sendDeleteNotification(String planetName) {
        log.info(format("Sending notification about planet delete. planet: {0}.", planetName));
        notificationMessagingTemplate.sendNotification("starwars-planet-delete", format("planet: {0}", planetName), "Planet being deleted");
        log.info(format("Notification sent. planet {0}.", planetName));
    }
}
