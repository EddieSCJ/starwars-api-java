//package com.api.starwars.planets.app.message.kafka;
//
//import com.api.starwars.planets.domain.message.MessageSender;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//public class KafkaManagerTest {
//
//    KafkaTemplate<String, String> messageSender;
//    MessageSender kafkaManager;
//
//    @BeforeEach
//    void setup() {
//        this.messageSender = Mockito.mock(KafkaTemplate.class);
//        this.kafkaManager = new KafkaManager(messageSender);
//    }
//
//    @Test
//    @DisplayName("Send kafka messages should work well")
//    void TestSendDeleteMessageSuccessful() {
//        kafkaManager.sendMessage("testando");
//        verify(messageSender, times(1)).send(anyString(), anyString());
//    }
//
//}
