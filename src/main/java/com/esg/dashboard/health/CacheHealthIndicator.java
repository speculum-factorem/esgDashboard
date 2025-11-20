package com.esg.dashboard.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheHealthIndicator implements HealthIndicator {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Health health() {
        try {
            String testKey = "health-check";
            String testValue = "ok";

            redisTemplate.opsForValue().set(testKey, testValue, 10, TimeUnit.SECONDS);
            String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);

            if (testValue.equals(retrievedValue)) {
                return Health.up()
                        .withDetail("cache", "operational")
                        .withDetail("keys", redisTemplate.getConnectionFactory().getConnection().dbSize())
                        .build();
            } else {
                return Health.down()
                        .withDetail("cache", "inconsistent")
                        .build();
            }
        } catch (Exception e) {
            log.error("Cache health check failed: {}", e.getMessage());
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}