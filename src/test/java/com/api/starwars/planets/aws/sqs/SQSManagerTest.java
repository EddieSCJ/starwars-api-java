package com.api.starwars.planets.aws.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SQSManagerTest {

    private AmazonSQS amazonSQS;
    private SQSManager sqsManager;

    @BeforeEach
    void setup() {
        this.amazonSQS = Mockito.mock(AmazonSQS.class);
        this.sqsManager = new SQSManager(amazonSQS);
    }

    @DisplayName("Send delete messages should work well")
    @Test
    void TestSendDeleteMessageSuccessful() {
        SendMessageResult result = new SendMessageResult();
        result.setMessageId("id");
        result.setMD5OfMessageBody("caa9c8f8620cbb30679026bb6427e11f");
        when(amazonSQS.sendMessage(any())).thenReturn(result);

        String messageId = sqsManager.sendDeleteMessage("testando");
        assertEquals(result.getMessageId(), messageId);
    }
}
