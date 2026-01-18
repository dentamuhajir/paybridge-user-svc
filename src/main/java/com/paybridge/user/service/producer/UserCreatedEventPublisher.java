package com.paybridge.user.service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paybridge.user.service.event.EventTopic;
import com.paybridge.user.service.event.UserCreatedEvent;
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
public class UserCreatedEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(UserCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            String traceId = MDC.get("trace_id");

            ProducerRecord<String, String> record = new ProducerRecord<>(
                    EventTopic.USER_CREATED,
                    event.getUserId(),
                    payload
            );

            if (traceId != null) {
                record.headers().add(
                        new RecordHeader(
                                "trace-id",
                                traceId.getBytes(StandardCharsets.UTF_8)
                        )
                );
            }

            kafkaTemplate.send(record);

            log.info(
                    "Published USER_CREATED event userId={}",
                    event.getUserId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to publish USER_CREATED userId={}",
                    event.getUserId(),
                    e
            );
            throw new KafkaPublishException(
                    "Failed to publish USER_CREATED event",
                    e
            );
        }
    }
}
