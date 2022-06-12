//package com.api.starwars.planets.app.message.aws;
//
//import com.api.starwars.planets.domain.message.MessageSender;
//import lombok.Setter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
//import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
//import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import static java.text.MessageFormat.format;
//
//@Slf4j
//@Service
//public class SQSManager implements MessageSender {
//
//    private final QueueMessagingTemplate queueMessagingTemplate;
//
//    @Setter
//    @Value("${cloud.aws.sqs.planet-delete-uri}")
//    private String planetDeleteQueueURL;
//
//    @Autowired
//    public SQSManager(QueueMessagingTemplate queueMessagingTemplate) {
//        this.queueMessagingTemplate = queueMessagingTemplate;
//    }
//a
//    public void sendMessage(String message) {
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("message-group-id", UUID.randomUUID().toString());
//
//        queueMessagingTemplate.convertAndSend(planetDeleteQueueURL, message, headers);
//        log.info(format("Message sent. message: {0}.", message));
//    }
//
//    @SqsListener(value = "planet-delete.fifo", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
//    public void processDeleteEventMessages(String message, @Header("SenderId") String senderId) {
//        log.info(format("SQS Planet Delete Message. senderId: {0}. messageBody: {1} ", senderId, message));
//    }
//}
