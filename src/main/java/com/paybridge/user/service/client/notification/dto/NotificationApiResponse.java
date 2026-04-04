package com.paybridge.user.service.client.notification.dto;

public record NotificationApiResponse<T>(
        boolean success,
        String message,
        T data
) {}
