package com.esg.dashboard.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId, WebSocketSession session) {
        activeSessions.put(sessionId, session);
        log.debug("WebSocket session added: {}. Total active sessions: {}", sessionId, activeSessions.size());
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
        log.debug("WebSocket session removed: {}. Total active sessions: {}", sessionId, activeSessions.size());
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    public Map<String, WebSocketSession> getActiveSessions() {
        return new ConcurrentHashMap<>(activeSessions);
    }

    public void broadcastToAll(String message) {
        activeSessions.forEach((sessionId, session) -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new org.springframework.web.socket.TextMessage(message));
                }
            } catch (Exception e) {
                log.warn("Failed to send message to session {}: {}", sessionId, e.getMessage());
            }
        });
    }
}