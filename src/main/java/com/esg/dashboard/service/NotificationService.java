package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для отправки уведомлений через WebSocket
 * Отправляет уведомления об изменениях рейтингов и позиций в рейтинге
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendRatingChangeNotification(Company company, Double previousScore, Double newScore) {
        try {
            MDC.put("companyId", company.getCompanyId());
            MDC.put("operation", "SEND_RATING_CHANGE_NOTIFICATION");
            log.info("Sending rating change notification for company: {} ({} -> {})",
                    company.getCompanyId(), previousScore, newScore);

            double change = newScore - previousScore;
            String changeType = change > 0 ? "improved" : "deteriorated";

            // Используем HashMap для поддержки null значений
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "RATING_CHANGE");
            notification.put("companyId", company.getCompanyId());
            notification.put("companyName", company.getName() != null ? company.getName() : "");
            notification.put("previousScore", previousScore);
            notification.put("newScore", newScore);
            notification.put("change", change);
            notification.put("changeType", changeType);
            notification.put("message", String.format("ESG rating %s from %.1f to %.1f", changeType, previousScore, newScore));
            notification.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/notifications", notification);
            log.debug("Rating change notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending rating change notification: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    public void sendRankingChangeNotification(String companyId, String companyName,
                                              Integer previousRank, Integer newRank) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "SEND_RANKING_CHANGE_NOTIFICATION");
            log.info("Sending ranking change notification for company: {} ({} -> {})",
                    companyId, previousRank, newRank);

            // Используем HashMap для поддержки null значений
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "RANKING_CHANGE");
            notification.put("companyId", companyId);
            notification.put("companyName", companyName != null ? companyName : "");
            notification.put("previousRank", previousRank);
            notification.put("newRank", newRank);
            notification.put("message", String.format("Ranking changed from %d to %d", previousRank, newRank));
            notification.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/notifications", notification);
            log.debug("Ranking change notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending ranking change notification: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}