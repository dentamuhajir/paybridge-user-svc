package com.paybridge.user.service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paybridge.user.service.event.EventTopic;
import com.paybridge.user.service.event.WalletCreateEvent;
import com.paybridge.user.service.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(WalletCreateEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    EventTopic.WALLET,
                    event.getUserId(), // key → ensures ordering per user
                    payload
            );

            log.info("Published WalletCreateEvent userId={}", event.getUserId());

        } catch (Exception e) {
            log.error("Kafka publish failed userId={}", event.getUserId(), e);
            throw new KafkaPublishException("Failed to publish wallet event", e);
        }
    }
}
