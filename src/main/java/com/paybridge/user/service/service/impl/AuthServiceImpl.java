package com.paybridge.user.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paybridge.user.service.client.WalletClient;
import com.paybridge.user.service.client.notification.dto.RegistrationEmailRequest;
import com.paybridge.user.service.common.response.ApiResponse;
import com.paybridge.user.service.dto.AuthResponse;
import com.paybridge.user.service.dto.LoginRequest;
import com.paybridge.user.service.dto.RegisterRequest;
import com.paybridge.user.service.dto.WalletCreateRequest;
import com.paybridge.user.service.entity.OutboxEvent;
import com.paybridge.user.service.entity.Role;
import com.paybridge.user.service.entity.User;
import com.paybridge.user.service.event.EventTopic;
import com.paybridge.user.service.event.UserCreatedEvent;
import com.paybridge.user.service.event.WalletCreateEvent;
import com.paybridge.user.service.producer.UserCreatedEventPublisher;
import com.paybridge.user.service.producer.WalletEventPublisher;
import com.paybridge.user.service.repository.OutboxEventRepository;
import com.paybridge.user.service.repository.RoleRepository;
import com.paybridge.user.service.repository.UserRepository;
import com.paybridge.user.service.security.AppUserDetailsService;
import com.paybridge.user.service.security.JwtService;
import com.paybridge.user.service.service.AuthService;
import com.paybridge.user.service.service.NotificationClient;
import jakarta.transaction.Transactional;
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
    private UserCreatedEventPublisher userCreatedEventPublisher;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OutboxEventRepository outboxEventRepository;
    @Autowired
    private NotificationClient notificationClient;

    @Transactional
    public ApiResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected: email already exists email={}", request.getEmail());
            throw new RuntimeException("Registration rejected: email already exists email=" + request.getEmail());
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

//        userCreatedEventPublisher.publish(
//                UserCreatedEvent.builder()
//                        .event("USER_CREATED")
//                        .userId(user.getId().toString())
//                        .occurredAt(java.time.Instant.now().toString())
//                        .build()
//        );

        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .event("USER_CREATED")
                    .userId(user.getId().toString())
                    .occurredAt(java.time.Instant.now().toString())
                    .build();

            OutboxEvent outbox = new OutboxEvent();
            outbox.setTopic(EventTopic.USER_CREATED);
            outbox.setAggregateId(user.getId().toString());
            outbox.setPayload(objectMapper.writeValueAsString(event));
            outboxEventRepository.save(outbox);

            log.info("Outbox event saved userId={}", user.getId());

        } catch (Exception e) {
            // Ini akan trigger rollback user juga karena @Transactional
            log.error("Failed to save outbox event userId={}", user.getId(), e);
            throw new RuntimeException("Registration failed, please try again");
        }

        try {
            RegistrationEmailRequest emailRequest = RegistrationEmailRequest.builder()
                    .to(user.getEmail())
                    .name(user.getFullName())
                    .verificationLink("https://paybridge.com/verify?token=" + user.getId())
                    .build();

            notificationClient.sendRegistrationEmail(emailRequest);
            log.info("Registration email sent userId={} email={}", user.getId(), user.getEmail());

        } catch (Exception e) {
            // Email failure should NOT fail the whole registration.
            // User is already saved — just log and move on.
            log.error("Failed to send registration email userId={} email={}", user.getId(), user.getEmail(), e);
        }

        log.info("Registration success email={} user_id={}", request.getEmail(), user.getId());

        log.info("Registration success email={} user_id={}",
                request.getEmail(), user.getId());

        return ApiResponse.success("Email registered successfully",null);
    }

    // Use HTTP request
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
            log.info("Invalid credentials of username={}", userDetails.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .build();

        return ApiResponse.success("Token generated", authResponse);
    }
}
