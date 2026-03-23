package com.paybridge.user.service.controller;

import com.paybridge.user.service.event.WalletCreateEvent;
import com.paybridge.user.service.producer.WalletEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/kafka-test")
@RequiredArgsConstructor
public class KafkaTestController {
    private final WalletEventPublisher publisher;
    @PostMapping("/wallet")
    public ResponseEntity<String> send() {
        WalletCreateEvent event = WalletCreateEvent.builder()
                .userId("user-123")
                .currency("IDR")
                .build();

        publisher.publish(event);
        return ResponseEntity.ok("sent");
    }

}
