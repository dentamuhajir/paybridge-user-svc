package com.paybridge.user.service.repository;

import com.paybridge.user.service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("""
        SELECT o FROM OutboxEvent o
        WHERE o.status = 'PENDING'
        AND o.retryCount < o.maxRetry
        ORDER BY o.createdAt ASC
        LIMIT 50
    """)
    List<OutboxEvent> findPendingEvents();
}