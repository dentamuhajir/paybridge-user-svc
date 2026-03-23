package com.paybridge.user.service.controller;

import com.paybridge.user.service.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping
    public ResponseEntity<?> health() {
        ApiResponse checkDatabase = checkDatabase();
        ApiResponse checkRedis = checkRedis();

        return ResponseEntity.status(200).body(
                ApiResponse.success("Health check completed",
                    Map.of(
                            "database", checkDatabase,
                            "redis",checkRedis
                    )
                )
        );
    }

    private ApiResponse checkDatabase() {
        logger.info("Health check: checking Database...");
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(5)) {
                logger.error("Database connection FAILED");
                return ApiResponse.error("Database connection failed", 500);
            }
        } catch (Exception e) {
            logger.error("Database health check error: {}", e.getMessage(), e);
            return ApiResponse.error("Database error: " + e.getMessage(), 500);
        }

        logger.info("Health check passed: Database OK");
        return ApiResponse.success("Database connection is healthy!", null);
    }

    private ApiResponse checkRedis() {
        logger.info("Health check: checking Redis...");
        try {
            String pong = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();

            if (!"PONG".equalsIgnoreCase(pong)) {
                logger.error("Redis PING returned: {}", pong);
                return ApiResponse.error("Redis connection failed", 500);
            }

        } catch (Exception e) {
            logger.error("Redis health check error: {}", e.getMessage(), e);
            return ApiResponse.error("Redis error: " + e.getMessage(), 500);
        }

        logger.info("Health check passed: Redis OK");
        return ApiResponse.success("Redis connection is healthy!", null);
    }
}
