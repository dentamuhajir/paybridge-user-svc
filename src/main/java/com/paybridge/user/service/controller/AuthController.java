package com.paybridge.user.service.controller;

import com.paybridge.user.service.common.response.ApiResponse;
import com.paybridge.user.service.dto.LoginRequest;
import com.paybridge.user.service.dto.RegisterRequest;
import com.paybridge.user.service.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Incoming request: Register user email={}", request.getEmail());
        ApiResponse resp = authService.register(request);
        log.info("Registration completed for email={} status={}", request.getEmail(), resp.getStatus());
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Incoming request: Login user email={}", request.getEmail());
        ApiResponse resp = authService.login(request);
        return ResponseEntity.status(resp.getStatus()).body(resp);
    }
}
