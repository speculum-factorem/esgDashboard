package com.esg.dashboard.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

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
                    .withDetail("mongodb", "available")
                    .withDetail("redis", "available")
                    .build();

        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}