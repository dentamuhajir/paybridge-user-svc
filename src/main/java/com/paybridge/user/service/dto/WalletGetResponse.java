package com.paybridge.user.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletGetResponse {
    @JsonProperty("user_id")
    private String userId;

    private Long balance;
    private String currency;
    private String status;
}
