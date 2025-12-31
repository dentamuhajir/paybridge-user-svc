package com.paybridge.user.service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paybridge.user.service.event.EventTopic;
import com.paybridge.user.service.event.WalletCreateEvent;
import com.paybridge.user.service.exception.KafkaPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(WalletCreateEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            String traceId = MDC.get("trace_id"); // MUST exist

            ProducerRecord<String, String> record = new ProducerRecord<>(
                    EventTopic.WALLET,
                    event.getUserId(),
                    payload
            );

            if (traceId != null) {
                record.headers().add(
                        new RecordHeader("trace-id", traceId.getBytes(StandardCharsets.UTF_8))
                );
            }

            kafkaTemplate.send(record);

            log.info("Published WalletCreateEvent userId={}", event.getUserId());

        } catch (Exception e) {
            log.error(
                    "Kafka publish failed userId={} traceId={}",
                    event.getUserId(),
                    MDC.get("trace_id"),
                    e
            );
            throw new KafkaPublishException("Failed to publish wallet event", e);
        }
    }
}
