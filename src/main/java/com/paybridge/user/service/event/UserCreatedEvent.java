package com.paybridge.user.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String event;        // "USER_CREATED"
    private String userId;
    private String occurredAt;   // ISO-8601
}
