package com.paybridge.user.service.service;

import com.paybridge.user.service.client.notification.dto.RegistrationEmailRequest;

public interface NotificationClient {
    void sendRegistrationEmail(RegistrationEmailRequest request);
}
