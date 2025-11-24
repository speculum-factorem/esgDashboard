package com.esg.dashboard.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Интерцептор для логирования всех входящих HTTP запросов
 * Добавляет traceId в MDC для трассировки запросов
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Генерируем уникальный traceId для трассировки запроса
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        log.info("Incoming request: {} {} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MDC.put("status", String.valueOf(response.getStatus()));
        log.info("Request completed: {} {} - Status: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus());

        if (ex != null) {
            log.error("Error processing request: {}", ex.getMessage(), ex);
        }

        MDC.clear();
    }
}