package com.esg.dashboard.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Health Indicator для проверки состояния баз данных
 * Проверяет доступность MongoDB и Redis
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;
    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try {
            MDC.put("operation", "DATABASE_HEALTH_CHECK");
            log.debug("Checking database health");

            // Проверка MongoDB
            mongoTemplate.executeCommand("{ ping: 1 }");
            log.debug("MongoDB is available");

            // Проверка Redis
            redisConnectionFactory.getConnection().ping();
            log.debug("Redis is available");

            return Health.up()
                    .withDetail("mongodb", "available")
                    .withDetail("redis", "available")
                    .build();

        } catch (Exception e) {
            log.error("Database health check failed: {}", e.getMessage(), e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        } finally {
            MDC.clear();
        }
    }
}