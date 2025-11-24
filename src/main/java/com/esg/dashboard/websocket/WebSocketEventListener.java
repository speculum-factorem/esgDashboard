package com.esg.dashboard.websocket;

import com.esg.dashboard.service.RealTimeUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Слушатель событий WebSocket соединений
 * Обрабатывает подключения и отключения клиентов
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RealTimeUpdateService realTimeUpdateService;
    private final RedisMessageListenerContainer redisContainer;

    private static final String ESG_UPDATES_CHANNEL = "esg:updates";

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        try {
            MDC.put("operation", "WEBSOCKET_CONNECT");
            log.info("New WebSocket connection established: {}", event.getMessage());

            // Подписываемся на канал Redis для новых соединений
            MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(realTimeUpdateService, "onMessage");
            redisContainer.addMessageListener(listenerAdapter, new PatternTopic(ESG_UPDATES_CHANNEL));

            log.debug("WebSocket client subscribed to Redis channel: {}", ESG_UPDATES_CHANNEL);
        } finally {
            MDC.clear();
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        try {
            MDC.put("operation", "WEBSOCKET_DISCONNECT");
            MDC.put("sessionId", event.getSessionId());
            log.info("WebSocket connection closed: {}", event.getSessionId());
        } finally {
            MDC.clear();
        }
    }
}