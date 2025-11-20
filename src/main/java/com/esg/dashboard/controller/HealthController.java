package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final MongoTemplate mongoTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        log.debug("Health check requested");

        boolean mongoHealthy = checkMongoDB();
        boolean redisHealthy = checkRedis();
        boolean overallHealthy = mongoHealthy && redisHealthy;

        Map<String, Object> healthStatus = Map.of(
                "status", overallHealthy ? "UP" : "DOWN",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "components", Map.of(
                        "mongodb", mongoHealthy ? "UP" : "DOWN",
                        "redis", redisHealthy ? "UP" : "DOWN"
                ),
                "details", Map.of(
                        "database", mongoHealthy ? "Connected" : "Disconnected",
                        "cache", redisHealthy ? "Connected" : "Disconnected"
                )
        );

        return ResponseEntity.ok(ApiResponse.success(healthStatus));
    }

    private boolean checkMongoDB() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return true;
        } catch (Exception e) {
            log.error("MongoDB health check failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean checkRedis() {
        try {
            redisConnectionFactory.getConnection().ping();
            return true;
        } catch (Exception e) {
            log.error("Redis health check failed: {}", e.getMessage());
            return false;
        }
    }
}