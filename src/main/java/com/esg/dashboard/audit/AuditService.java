package com.esg.dashboard.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для аудита действий пользователей
 * Логирует все важные операции для обеспечения прозрачности и соответствия требованиям
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final MongoTemplate mongoTemplate;

    @Async("taskExecutor")
    public void logAction(String action, String entityType, String entityId, String userId, Map<String, Object> details) {
        try {
            MDC.put("operation", "LOG_AUDIT_ACTION");
            MDC.put("action", action);
            MDC.put("entityType", entityType);
            MDC.put("entityId", entityId);
            MDC.put("userId", userId);
            
            log.debug("Saving audit log: {} for {} (ID: {})", action, entityType, entityId);

            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .userId(userId)
                    .details(details != null ? details : new HashMap<>())
                    .timestamp(LocalDateTime.now())
                    .build();

            mongoTemplate.save(auditLog, "audit_logs");
            log.debug("Audit log successfully saved: {} for {}", action, entityType);
        } catch (Exception e) {
            log.error("Error saving audit log: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    @Async("taskExecutor")
    public void logRatingChange(String companyId, Double previousScore, Double newScore, String changedBy) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "LOG_RATING_CHANGE");
            log.debug("Logging rating change for company: {} ({} -> {})", 
                    companyId, previousScore, newScore);

            // Используем HashMap для поддержки null значений
            Map<String, Object> details = new HashMap<>();
            details.put("previousScore", previousScore);
            details.put("newScore", newScore);
            details.put("change", newScore - previousScore);

            logAction("RATING_UPDATE", "COMPANY", companyId, changedBy, details);
        } finally {
            MDC.clear();
        }
    }

    @Async("taskExecutor")
    public void logPortfolioUpdate(String portfolioId, String action, String userId) {
        try {
            MDC.put("portfolioId", portfolioId);
            MDC.put("operation", "LOG_PORTFOLIO_UPDATE");
            log.debug("Logging portfolio update: {} (action: {})", portfolioId, action);

            logAction(action, "PORTFOLIO", portfolioId, userId, new HashMap<>());
        } finally {
            MDC.clear();
        }
    }
}