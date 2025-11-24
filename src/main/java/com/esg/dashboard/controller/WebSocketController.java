package com.esg.dashboard.controller;

import com.esg.dashboard.dto.RealTimeUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * Контроллер для обработки WebSocket сообщений
 */
@Slf4j
@Controller
public class WebSocketController {

    @MessageMapping("/esg.updates")
    @SendTo("/topic/esg-updates")
    public RealTimeUpdateDto handleEsgUpdate(RealTimeUpdateDto update) {
        try {
            MDC.put("operation", "HANDLE_ESG_UPDATE");
            log.debug("Received WebSocket message: {}", update);

            // Добавляем временную метку если она отсутствует
            if (update.getTimestamp() == null) {
                update.setTimestamp(LocalDateTime.now());
            }

            return update;
        } finally {
            MDC.clear();
        }
    }

    @MessageMapping("/notifications.subscribe")
    @SendTo("/topic/notifications")
    public String handleNotificationSubscription(String clientId) {
        try {
            MDC.put("clientId", clientId);
            MDC.put("operation", "HANDLE_NOTIFICATION_SUBSCRIBE");
            log.info("Client subscribed to notifications: {}", clientId);
            return "Subscribed to notifications: " + clientId;
        } finally {
            MDC.clear();
        }
    }
}