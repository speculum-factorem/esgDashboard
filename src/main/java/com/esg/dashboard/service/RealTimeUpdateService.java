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
            MDC.put("operation", "PUBLISH_COMPANY_UPDATE");
            log.info("Publishing real-time update for company: {}", company.getCompanyId());

            // Используем HashMap вместо Map.of() для поддержки null значений
            Map<String, Object> updateMessage = new java.util.HashMap<>();
            updateMessage.put("type", "COMPANY_UPDATE");
            updateMessage.put("companyId", company.getCompanyId());
            updateMessage.put("companyName", company.getName() != null ? company.getName() : "");
            updateMessage.put("sector", company.getSector() != null ? company.getSector() : "");
            
            // Добавляем рейтинг только если он не null
            if (company.getCurrentRating() != null) {
                updateMessage.put("rating", company.getCurrentRating());
            }
            
            updateMessage.put("timestamp", System.currentTimeMillis());

            String messageJson = objectMapper.writeValueAsString(updateMessage);
            redisTemplate.convertAndSend(ESG_UPDATES_CHANNEL, messageJson);

            log.debug("Update message published to channel: {}", ESG_UPDATES_CHANNEL);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize update message: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to publish company update: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    public void publishRatingUpdate(String companyId, ESGRating newRating) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "PUBLISH_RATING_UPDATE");
            log.info("Publishing rating update for company: {}", companyId);

            // Используем HashMap вместо Map.of() для поддержки null значений
            Map<String, Object> updateMessage = new java.util.HashMap<>();
            updateMessage.put("type", "RATING_UPDATE");
            updateMessage.put("companyId", companyId);
            
            // Добавляем рейтинг только если он не null
            if (newRating != null) {
                updateMessage.put("newRating", newRating);
            }
            
            updateMessage.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/esg-updates", updateMessage);
            log.debug("Rating update sent via WebSocket");
        } catch (Exception e) {
            log.error("Failed to publish rating update: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            MDC.put("operation", "REDIS_MESSAGE_RECEIVED");
            String channel = new String(pattern);
            String body = new String(message.getBody());

            log.debug("Received Redis message from channel: {}", channel);

            // Транслируем сообщение всем WebSocket клиентам
            messagingTemplate.convertAndSend("/topic/esg-updates", body);
            log.debug("Message broadcast to WebSocket clients");
        } catch (Exception e) {
            log.error("Error processing Redis message: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}