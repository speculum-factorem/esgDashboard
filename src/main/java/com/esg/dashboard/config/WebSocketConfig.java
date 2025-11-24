package com.esg.dashboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Конфигурация WebSocket для real-time обновлений ESG данных
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.websocket.endpoints:/ws-esg}")
    private String websocketEndpoint;

    @Value("${app.websocket.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Включаем простой брокер сообщений для топиков
        config.enableSimpleBroker("/topic");
        // Префикс для сообщений от клиента к серверу
        config.setApplicationDestinationPrefixes("/app");
        log.info("WebSocket message broker configured: topics /topic, application prefix /app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Регистрируем STOMP endpoint с поддержкой SockJS для обратной совместимости
        String[] origins = allowedOrigins.split(",");
        registry.addEndpoint(websocketEndpoint)
                .setAllowedOriginPatterns(origins)
                .withSockJS();
        log.info("WebSocket endpoint registered: {} with allowed origins: {}", websocketEndpoint, allowedOrigins);
    }
}