package com.api.starwars.planets.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.api.starwars.planets.handler.interfaces.ISQSManager;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.text.MessageFormat.format;

@Slf4j
@Service
@Configuration
@EnableScheduling
public class SQSManager implements ISQSManager {

    @Qualifier("getSQSClient")
    private final AmazonSQS amazonSQS;

    @Value("${aws.sqs.planet.delete.url}")
    private String planetDeleteQueueURL;

    @Autowired
    public SQSManager(AmazonSQS amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    public String sendDeleteMessage(String planetName) {
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

    @Scheduled(fixedDelay = 60000 * 60 * 24)
    public void processDeleteEventMessages() {
        log.info("Start reading planet delete event messages");
        List<Message> messages = this.amazonSQS.receiveMessage(planetDeleteQueueURL).getMessages();
        for (Message m : messages) {
            log.info(format("SQS Planet Delete Message. messageId: {0}. messageBody: {1} ", m.getMessageId(), m.getBody()));
            this.amazonSQS.deleteMessage(planetDeleteQueueURL, m.getReceiptHandle());
        }
    }
}
