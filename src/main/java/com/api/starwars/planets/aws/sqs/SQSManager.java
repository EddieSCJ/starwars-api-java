package com.api.starwars.planets.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.api.starwars.planets.handler.interfaces.ISQSManager;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.text.MessageFormat.format;

@Slf4j
@Service
public class SQSManager implements ISQSManager {

    @Qualifier("getSQSClient")
    private final AmazonSQS amazonSQS;

    @Value("${aws.sqs.planet.delete.url}")
    private String planetDeleteQueueURL;

    @Autowired
    public SQSManager(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public String sendDeleteEvent(String planetName) {
        Map<String, String> body = new HashMap<>();
        body.put("event", "planet-delete");
        body.put("planet_name", planetName);

        JSONObject bodyMessage = new JSONObject(body);

        SendMessageRequest sendDeleteEventMessage = new SendMessageRequest()
                .withQueueUrl(planetDeleteQueueURL)
                .withMessageBody(bodyMessage.toJSONString())
                .withMessageGroupId(UUID.randomUUID().toString());

        SendMessageResult result = amazonSQS.sendMessage(sendDeleteEventMessage);
        log.info(format("Planet delete event sent. planetName: {0}. messageId: {1}", planetName, result.getMessageId()));
        return result.getMessageId();
    }
}
