package com.esg.dashboard.filter;

import com.esg.dashboard.config.RateLimitConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Фильтр для ограничения частоты запросов (rate limiting)
 * Использует Redis для хранения счетчиков запросов
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RateLimitConfig rateLimitConfig;

    private static final String RATE_LIMIT_MINUTE_PREFIX = "rate_limit:minute:";
    private static final String RATE_LIMIT_HOUR_PREFIX = "rate_limit:hour:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!rateLimitConfig.isRateLimitEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            MDC.put("operation", "RATE_LIMIT_CHECK");
            String clientId = getClientId(request);
            MDC.put("clientId", clientId);

            String minuteKey = RATE_LIMIT_MINUTE_PREFIX + clientId;
            String hourKey = RATE_LIMIT_HOUR_PREFIX + clientId;

            ValueOperations<String, Object> ops = redisTemplate.opsForValue();

            // Проверяем лимит на минуту
            Long minuteCount = ops.increment(minuteKey);
            if (minuteCount == 1) {
                redisTemplate.expire(minuteKey, 1, TimeUnit.MINUTES);
            }

            // Проверяем лимит на час
            Long hourCount = ops.increment(hourKey);
            if (hourCount == 1) {
                redisTemplate.expire(hourKey, 1, TimeUnit.HOURS);
            }

            // Устанавливаем заголовки
            long remainingMinute = Math.max(0, rateLimitConfig.getRequestsPerMinute() - minuteCount);
            long remainingHour = Math.max(0, rateLimitConfig.getRequestsPerHour() - hourCount);
            response.setHeader("X-RateLimit-Remaining-Minute", String.valueOf(remainingMinute));
            response.setHeader("X-RateLimit-Limit-Minute", String.valueOf(rateLimitConfig.getRequestsPerMinute()));
            response.setHeader("X-RateLimit-Remaining-Hour", String.valueOf(remainingHour));
            response.setHeader("X-RateLimit-Limit-Hour", String.valueOf(rateLimitConfig.getRequestsPerHour()));

            if (minuteCount > rateLimitConfig.getRequestsPerMinute() || 
                hourCount > rateLimitConfig.getRequestsPerHour()) {
                // Превышен лимит запросов
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setHeader("Retry-After", "60");
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
                log.warn("Rate limit exceeded for client: {} - minute: {}/{}, hour: {}/{}", 
                        clientId, minuteCount, rateLimitConfig.getRequestsPerMinute(), 
                        hourCount, rateLimitConfig.getRequestsPerHour());
                return;
            }

            log.debug("Request allowed for client: {} - minute: {}/{}, hour: {}/{}", 
                    clientId, minuteCount, rateLimitConfig.getRequestsPerMinute(), 
                    hourCount, rateLimitConfig.getRequestsPerHour());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in rate limit filter: {}", e.getMessage(), e);
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Получает идентификатор клиента из запроса
     * Использует IP адрес или username из SecurityContext
     */
    private String getClientId(HttpServletRequest request) {
        // Пытаемся получить username из SecurityContext
        if (request.getUserPrincipal() != null) {
            return request.getUserPrincipal().getName();
        }
        // Иначе используем IP адрес
        return request.getRemoteAddr();
    }
}

