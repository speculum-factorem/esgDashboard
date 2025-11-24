package com.esg.dashboard.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Health Indicator для проверки подключения к базам данных
 * Проверяет подключение к MongoDB и Redis с временной меткой
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseConnectivityHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            MDC.put("operation", "DATABASE_CONNECTIVITY_CHECK");
            log.debug("Checking database connectivity");

            // Проверка MongoDB
            mongoTemplate.executeCommand("{ ping: 1 }");
            log.debug("MongoDB connected");

            // Проверка Redis
            redisConnectionFactory.getConnection().ping();
            log.debug("Redis connected");

            return Health.up()
                    .withDetail("mongodb", "connected")
                    .withDetail("redis", "connected")
                    .withDetail("timestamp", LocalDateTime.now().toString())
                    .build();

        } catch (Exception e) {
            log.error("Database connectivity check failed: {}", e.getMessage(), e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now().toString())
                    .build();
        } finally {
            MDC.clear();
        }
    }
}