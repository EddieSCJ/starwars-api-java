package com.api.starwars.planets.aws.sqs;

import com.amazonaws.services.sqs.model.SendMessageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SQSManagerTest {

    private QueueMessagingTemplate queueMessagingTemplate;
    private SQSManager sqsManager;

    @BeforeEach
    void setup() {
        this.queueMessagingTemplate = Mockito.mock(QueueMessagingTemplate.class);
        this.sqsManager = new SQSManager(queueMessagingTemplate);
        this.sqsManager.setPlanetDeleteQueueURL("testUrl");
    }

    @DisplayName("Send delete messages should work well")
    @Test
    void TestSendDeleteMessageSuccessful() {
        SendMessageResult result = new SendMessageResult();
        result.setMessageId("id");
        result.setMD5OfMessageBody("caa9c8f8620cbb30679026bb6427e11f");

        sqsManager.sendDeleteMessage("testando");
        verify(queueMessagingTemplate, times(1)).convertAndSend(anyString(), any(), anyMap());
    }
}
