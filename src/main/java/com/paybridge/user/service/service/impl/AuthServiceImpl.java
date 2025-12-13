package com.paybridge.user.service.service.impl;

import com.paybridge.user.service.client.WalletClient;
import com.paybridge.user.service.common.response.ApiResponse;
import com.paybridge.user.service.dto.AuthResponse;
import com.paybridge.user.service.dto.LoginRequest;
import com.paybridge.user.service.dto.RegisterRequest;
import com.paybridge.user.service.dto.WalletCreateRequest;
import com.paybridge.user.service.entity.Role;
import com.paybridge.user.service.entity.User;
import com.paybridge.user.service.event.WalletCreateEvent;
import com.paybridge.user.service.producer.WalletEventPublisher;
import com.paybridge.user.service.repository.RoleRepository;
import com.paybridge.user.service.repository.UserRepository;
import com.paybridge.user.service.security.AppUserDetailsService;
import com.paybridge.user.service.security.JwtService;
import com.paybridge.user.service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AppUserDetailsService userDetailsService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private WalletClient walletClient;
    @Autowired
    private WalletEventPublisher walletEventPublisher;

    public ApiResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected: email already exists email={}", request.getEmail());
            return ApiResponse.error("Email has already registered", 409);
        }

        Role userRole = roleRepository.findByName("USER");

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(userRole);
        userRepository.save(user);
        log.info("User saved with id={} email={}", user.getId(), user.getEmail());

        // Resilient wallet creation (post-tx, fire-and-forget)
        walletEventPublisher.publish(
                new WalletCreateEvent(user.getId().toString(), "IDR")
        );

        //createWalletForUser(user.getId().toString());

        log.info("Registration success email={} user_id={}",
                request.getEmail(), user.getId());

        return ApiResponse.success("Email registered successfully",null);
    }

    private void createWalletForUser(String userId) {
        log.info("Initiating wallet creation user_id={}", userId);

        try {
            WalletCreateRequest walletReq = new WalletCreateRequest();
            walletReq.setUserId(userId);
            walletReq.setCurrency("IDR");
            walletClient.createWallet(walletReq);

            log.info("Wallet creation request sent user_id={}", userId);
        } catch (Exception e) {
            log.error("Wallet creation failed user_id={} error={}", userId, e.getMessage(), e);
            // Optional: Emit event for retry/notify (e.g., via Spring Events or Kafka)
            // Don't rethrow—registration succeeds
        }
    }

    public ApiResponse login(LoginRequest request) {
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        return ApiResponse.success("Token generated", authResponse);
    }
}
