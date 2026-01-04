package com.paybridge.user.service.client;

import com.paybridge.user.service.dto.WalletCreateRequest;  // Assume this DTO exists
import com.paybridge.user.service.dto.WalletGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class WalletClient {

    private final RestTemplate restTemplate;

    private final String walletEndpoint;

    private final String transactionToken;

    public WalletClient(RestTemplate restTemplate,
                        @Value("${services.transaction.endpoint}") String transactionServiceEndpoint,
                        @Value("${tokens.transaction-service}") String transactionToken) {
        this.restTemplate = restTemplate;
        this.walletEndpoint = transactionServiceEndpoint;
        this.transactionToken = transactionToken;
        log.info("WalletClient initialized: URL={}, Token length={}", walletEndpoint, transactionToken.length());
    }

    public void createWallet(WalletCreateRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + transactionToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String traceId = MDC.get("trace_id");
            if (traceId != null) headers.set("X-Trace-Id", traceId);
            HttpEntity<WalletCreateRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForObject(walletEndpoint , entity, Void.class);
            log.info("Wallet creation request sent for user: {}", request.getUserId());
        } catch (Exception e) {
            log.error("Failed to create wallet for user {}: {}", request.getUserId(), e.getMessage());
            throw new RuntimeException("Wallet creation failed", e);  // Or handle gracefully
        }
    }

    public WalletGetResponse getWallet(String userId) {
        String url = walletEndpoint + "/internal/wallet/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + transactionToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String traceId = MDC.get("trace_id");
        if (traceId != null) headers.set("X-Trace-Id", traceId);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                WalletGetResponse.class
        ).getBody();
    }
}