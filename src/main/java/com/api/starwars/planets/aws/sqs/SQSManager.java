package com.api.starwars.planets.aws.sqs;

import com.api.starwars.planets.storage.interfaces.ISQSManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.text.MessageFormat.format;

@Slf4j
@Service
public class SQSManager implements ISQSManager {

    private final QueueMessagingTemplate queueMessagingTemplate;

    @Setter
    @Value("${cloud.aws.sqs.planet-delete-uri}")
    private String planetDeleteQueueURL;

    @Autowired
    public SQSManager(QueueMessagingTemplate queueMessagingTemplate) {
        this.queueMessagingTemplate = queueMessagingTemplate;
    }

    public void sendDeleteMessage(String planetName) {
        Map<String, String> body = new HashMap<>();
        body.put("event", "planet-delete");
        body.put("planet_name", planetName);

        JSONObject bodyMessage = new JSONObject(body);

        Map<String, Object> headers = new HashMap<>();
        headers.put("message-group-id", UUID.randomUUID().toString());

        queueMessagingTemplate.convertAndSend(planetDeleteQueueURL, bodyMessage.toJSONString(), headers);
        log.info(format("Planet delete event sent. planetName: {0}.", planetName));
    }

    @SqsListener(value = "planet-delete.fifo", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void processDeleteEventMessages(String message, @Header("SenderId") String senderId) {
        log.info(format("SQS Planet Delete Message. senderId: {0}. messageBody: {1} ", senderId, message));
    }
}
