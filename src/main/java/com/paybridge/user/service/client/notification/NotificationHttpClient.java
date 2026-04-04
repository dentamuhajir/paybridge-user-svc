package com.paybridge.user.service.client.notification;

import com.paybridge.user.service.client.notification.dto.NotificationApiResponse;
import com.paybridge.user.service.client.notification.dto.RegistrationEmailRequest;
import com.paybridge.user.service.service.NotificationClient;
import io.micrometer.observation.ObservationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Configuration
@Slf4j
public class NotificationHttpClient implements NotificationClient {
    private static final ParameterizedTypeReference<NotificationApiResponse<Object>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final WebClient webClient;
    private final String notificationBaseUrl;

    public NotificationHttpClient(
            WebClient.Builder builder,
            ObservationRegistry observationRegistry,
            @Value("${services.notification.endpoint}") String baseUrl
    ) {
        this.notificationBaseUrl = baseUrl;
        this.webClient = builder
                .baseUrl(baseUrl)
                .observationRegistry(observationRegistry)
                .build();
    }

    @Override
    public void sendRegistrationEmail(RegistrationEmailRequest request) {
        String path = NotificationApiPaths.SEND_EMAIL_REGISTRATION;
        log.info(
                "Notification registration email: calling POST {}{} to={}",
                notificationBaseUrl,
                path,
                request.getTo());

        long startNanos = System.nanoTime();
        NotificationApiResponse<Object> response = webClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(ex -> log.warn(
                        "Notification registration email: HTTP error target={}{} to={} message={}",
                        notificationBaseUrl,
                        path,
                        request.getTo(),
                        ex.getMessage(),
                        ex))
                .block(Duration.ofSeconds(5));

        long durationMs = Duration.ofNanos(System.nanoTime() - startNanos).toMillis();
        if (response != null) {
            log.info(
                    "Notification registration email: completed to={} success={} apiMessage={} durationMs={}",
                    request.getTo(),
                    response.success(),
                    response.message(),
                    durationMs);
        } else {
            log.warn(
                    "Notification registration email: empty response to={} durationMs={}",
                    request.getTo(),
                    durationMs);
        }
    }
}
