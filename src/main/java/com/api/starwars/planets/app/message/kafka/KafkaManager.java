//package com.api.starwars.planets.app.message.kafka;
//
//import com.api.starwars.planets.domain.message.MessageSender;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//import static java.text.MessageFormat.format;
//
//@Slf4j
//@Service
//public class KafkaManager implements MessageSender {
//    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final String topicName = "planet-event";
//
//    @Autowired
//    public KafkaManager(final KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendMessage(String message) {
//        log.info(format("Starting send event via kafka producer. message: {0}", message));
//        kafkaTemplate.send(topicName, message);
//        log.info(format("Message sent event kafka producer. message: {0}", message));
//    }
//
//    //if you need add group id just type `, groupId="value"`
//    @KafkaListener(topics = "planet-event")
//    public void consumePlanetEvent(String message) {
//        log.info(format("Planet event received via kafka. message: {0}", message));
//    }
//}
