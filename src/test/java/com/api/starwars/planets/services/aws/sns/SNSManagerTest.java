package com.api.starwars.planets.services.aws.sns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
class SNSManagerTest {

    private NotificationMessagingTemplate notificationMessagingTemplate;
    private SNSSender snsManager;

    @BeforeEach
    void setup() {
        this.notificationMessagingTemplate = Mockito.mock(NotificationMessagingTemplate.class);
        this.snsManager = new SNSSender(notificationMessagingTemplate);
    }

    @DisplayName("Send delete notification should work well")
    @Test
    void TestSendDeleteNotificationSuccessful() {

        this.snsManager.sendNotification("testando", "testando");
        verify(this.notificationMessagingTemplate, times(1)).sendNotification(anyString(), anyString(), anyString());
    }
}
