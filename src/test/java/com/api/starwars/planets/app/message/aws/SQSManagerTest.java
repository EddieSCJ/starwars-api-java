package com.api.starwars.planets.app.message.aws;

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
class SQSManagerTest {

    private QueueMessagingTemplate queueMessagingTemplate;
    private SQSManager sqsManager;

    @BeforeEach
    void setup() {
        this.queueMessagingTemplate = Mockito.mock(QueueMessagingTemplate.class);
        this.sqsManager = new SQSManager(queueMessagingTemplate);
        this.sqsManager.setPlanetDeleteQueueURL("testUrl");
    }

    @Test
    @DisplayName("Send sqs messages should work well")
    void TestSendDeleteMessageSuccessful() {
        sqsManager.sendMessage("testando");
        verify(queueMessagingTemplate, times(1)).convertAndSend(anyString(), any(), anyMap());
    }
}
