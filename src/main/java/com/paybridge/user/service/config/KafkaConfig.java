package com.paybridge.user.service.config;

import jakarta.validation.constraints.Null;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaAdmin kafkaAdmin(KafkaProperties properties) {
        return new KafkaAdmin(properties.buildAdminProperties(null));
    }
}
