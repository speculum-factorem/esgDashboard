package com.esg.dashboard.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для rate limiting
 */
@Slf4j
@Configuration
@Data
public class RateLimitConfig {

    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${app.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;

    @Value("${app.rate-limit.requests-per-hour:1000}")
    private int requestsPerHour;

    public RateLimitConfig() {
        log.info("Rate limit configuration initialized - enabled: {}, per minute: {}, per hour: {}", 
                rateLimitEnabled, requestsPerMinute, requestsPerHour);
    }
}

