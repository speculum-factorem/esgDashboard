package com.esg.dashboard.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Конфигурация для добавления заголовков безопасности
 */
@Slf4j
@Configuration
public class SecurityHeadersConfig extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        // Защита от XSS атак
        response.setHeader("X-Content-Type-Options", "nosniff");
        // Защита от clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        // Политика безопасности контента
        response.setHeader("Content-Security-Policy", "default-src 'self'");
        // Строгий транспорт безопасности (для HTTPS)
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        // Отключение кэширования для чувствительных данных
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        
        filterChain.doFilter(request, response);
    }
}

