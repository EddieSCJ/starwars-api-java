//package com.api.starwars.planets.app.message.aws;
//
//import com.api.starwars.planets.domain.message.NotificationSender;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//import static java.text.MessageFormat.format;
//
//@Slf4j
//@Service
//public class SNSManager implements NotificationSender {
//
//    private final NotificationMessagingTemplate notificationMessagingTemplate;
//
//    @Autowired
//    public SNSManager(NotificationMessagingTemplate notificationMessagingTemplate) {
//        this.notificationMessagingTemplate = notificationMessagingTemplate;
//    }
//
//    public void sendNotification(String subject, String message) {
//        log.info(format("Sending notification via SNS. subject: {0}. message: {1}.", subject, message));
//        notificationMessagingTemplate.sendNotification("starwars-planet-delete", message, "Planet being deleted");
//        log.info(format("Notification sent via SNS. subject: {0}. message: {1}", subject, message));
//    }
//}
