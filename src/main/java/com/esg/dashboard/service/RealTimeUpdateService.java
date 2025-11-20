package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeUpdateService implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String ESG_UPDATES_CHANNEL = "esg:updates";

    public void publishCompanyUpdate(Company company) {
        try {
            MDC.put("companyId", company.getCompanyId());
            log.info("Publishing real-time update for company: {}", company.getCompanyId());

            Map<String, Object> updateMessage = Map.of(
                    "type", "COMPANY_UPDATE",
                    "companyId", company.getCompanyId(),
                    "companyName", company.getName(),
                    "rating", company.getCurrentRating(),
                    "timestamp", System.currentTimeMillis()
            );

            String messageJson = objectMapper.writeValueAsString(updateMessage);
            redisTemplate.convertAndSend(ESG_UPDATES_CHANNEL, messageJson);

            log.debug("Update message published to channel: {}", ESG_UPDATES_CHANNEL);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize update message: {}", e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    public void publishRatingUpdate(String companyId, ESGRating newRating) {
        try {
            MDC.put("companyId", companyId);
            log.info("Publishing rating update for company: {}", companyId);

            Map<String, Object> updateMessage = Map.of(
                    "type", "RATING_UPDATE",
                    "companyId", companyId,
                    "newRating", newRating,
                    "timestamp", System.currentTimeMillis()
            );

            messagingTemplate.convertAndSend("/topic/esg-updates", updateMessage);
            log.debug("Rating update sent via WebSocket");
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(pattern);
            String body = new String(message.getBody());

            log.debug("Received Redis message from channel: {}", channel);

            // Broadcast to WebSocket clients
            messagingTemplate.convertAndSend("/topic/esg-updates", body);
            log.debug("Message broadcast to WebSocket clients");
        } catch (Exception e) {
            log.error("Error processing Redis message: {}", e.getMessage());
        }
    }
}