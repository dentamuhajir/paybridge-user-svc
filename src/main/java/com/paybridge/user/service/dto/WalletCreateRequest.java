package com.paybridge.user.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WalletCreateRequest {
    @JsonProperty("user_id")
    @NotBlank
    private String userId;
    @NotBlank
    private String currency;
}
