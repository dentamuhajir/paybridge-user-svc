package com.paybridge.user.service.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {
    String token;
}
