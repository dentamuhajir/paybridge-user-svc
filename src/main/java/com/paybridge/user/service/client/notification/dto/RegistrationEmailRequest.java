package com.paybridge.user.service.client.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationEmailRequest {

    @JsonProperty("to")
    private String to;

    @JsonProperty("name")
    private String name;

    @JsonProperty("verification_link")
    private String verificationLink;
}
