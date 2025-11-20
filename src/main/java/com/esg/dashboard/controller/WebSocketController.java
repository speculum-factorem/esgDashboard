package com.esg.dashboard.controller;

import com.esg.dashboard.dto.RealTimeUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Slf4j
@Controller
public class WebSocketController {

    @MessageMapping("/esg.updates")
    @SendTo("/topic/esg-updates")
    public RealTimeUpdateDto handleEsgUpdate(RealTimeUpdateDto update) {
        log.debug("Received WebSocket message: {}", update);

        // Add timestamp if not present
        if (update.getTimestamp() == null) {
            update.setTimestamp(LocalDateTime.now());
        }

        return update;
    }

    @MessageMapping("/notifications.subscribe")
    @SendTo("/topic/notifications")
    public String handleNotificationSubscription(String clientId) {
        log.info("Client subscribed to notifications: {}", clientId);
        return "Subscribed to notifications: " + clientId;
    }
}