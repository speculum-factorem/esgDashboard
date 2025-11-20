package com.esg.dashboard.websocket;

import com.esg.dashboard.service.RealTimeUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
        log.info("New WebSocket connection established: {}", event.getMessage());

        // Subscribe to Redis channel for new connections
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(realTimeUpdateService, "onMessage");
        redisContainer.addMessageListener(listenerAdapter, new PatternTopic(ESG_UPDATES_CHANNEL));

        log.debug("WebSocket client subscribed to Redis channel: {}", ESG_UPDATES_CHANNEL);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        log.info("WebSocket connection closed: {}", event.getSessionId());
    }
}