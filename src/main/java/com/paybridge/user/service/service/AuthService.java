package com.paybridge.user.service.service;

import com.paybridge.user.service.common.response.ApiResponse;
import com.paybridge.user.service.dto.LoginRequest;
import com.paybridge.user.service.dto.RegisterRequest;

public interface AuthService {
    public ApiResponse register(RegisterRequest request);
    public ApiResponse login(LoginRequest request);
}
