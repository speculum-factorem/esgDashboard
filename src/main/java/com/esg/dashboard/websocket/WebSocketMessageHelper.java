package com.esg.dashboard.websocket;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageHelper {

    private final ObjectMapper objectMapper;

    public String createCompanyUpdateMessage(Company company) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "COMPANY_UPDATE",
                    "companyId", company.getCompanyId(),
                    "companyName", company.getName(),
                    "sector", company.getSector(),
                    "rating", company.getCurrentRating(),
                    "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to create company update message: {}", e.getMessage());
            return "{}";
        }
    }

    public String createRatingUpdateMessage(String companyId, ESGRating newRating) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "RATING_UPDATE",
                    "companyId", companyId,
                    "newRating", newRating,
                    "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to create rating update message: {}", e.getMessage());
            return "{}";
        }
    }

    public String createErrorMessage(String error, String details) {
        try {
            Map<String, Object> message = Map.of(
                    "type", "ERROR",
                    "error", error,
                    "details", details,
                    "timestamp", System.currentTimeMillis()
            );
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to create error message: {}", e.getMessage());
            return "{\"type\":\"ERROR\",\"error\":\"Message serialization failed\"}";
        }
    }
}