package com.esg.dashboard.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final MongoTemplate mongoTemplate;

    @Async("taskExecutor")
    public void logAction(String action, String entityType, String entityId, String userId, Map<String, Object> details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .userId(userId)
                    .details(details)
                    .timestamp(LocalDateTime.now())
                    .build();

            mongoTemplate.save(auditLog, "audit_logs");
            log.debug("Audit log saved: {} for {}", action, entityType);
        } catch (Exception e) {
            log.error("Failed to save audit log: {}", e.getMessage());
        }
    }

    @Async("taskExecutor")
    public void logRatingChange(String companyId, Double previousScore, Double newScore, String changedBy) {
        Map<String, Object> details = Map.of(
                "previousScore", previousScore,
                "newScore", newScore,
                "change", newScore - previousScore
        );

        logAction("RATING_UPDATE", "COMPANY", companyId, changedBy, details);
    }

    @Async("taskExecutor")
    public void logPortfolioUpdate(String portfolioId, String action, String userId) {
        logAction(action, "PORTFOLIO", portfolioId, userId, Map.of());
    }
}