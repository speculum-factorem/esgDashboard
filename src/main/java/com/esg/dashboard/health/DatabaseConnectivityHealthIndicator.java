package com.esg.dashboard.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectivityHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            // Check MongoDB
            mongoTemplate.executeCommand("{ ping: 1 }");

            // Check Redis
            redisConnectionFactory.getConnection().ping();

            return Health.up()
                    .withDetail("mongodb", "connected")
                    .withDetail("redis", "connected")
                    .withDetail("timestamp", LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Database connectivity health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now().toString())
                    .build();
        }
    }
}