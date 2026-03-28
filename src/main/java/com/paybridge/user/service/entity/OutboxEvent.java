package com.paybridge.user.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "outbox_events", indexes = {
        @Index(name = "idx_outbox_status", columnList = "status, createdAt")
})
public class OutboxEvent {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false, name = "aggregate_id")
    private String aggregateId;

    @Column(nullable = false)
    private String aggregatetype;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, name = "retry_count")
    private int retryCount;

    @Column(nullable = false, name = "max_retry")
    private int maxRetry;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        status = "PENDING";
        retryCount = 0;
        maxRetry = 5;
        aggregatetype = "user";
    }
}