package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendRatingChangeNotification(Company company, Double previousScore, Double newScore) {
        double change = newScore - previousScore;
        String changeType = change > 0 ? "improved" : "deteriorated";

        Map<String, Object> notification = Map.of(
                "type", "RATING_CHANGE",
                "companyId", company.getCompanyId(),
                "companyName", company.getName(),
                "previousScore", previousScore,
                "newScore", newScore,
                "change", change,
                "changeType", changeType,
                "message", String.format("ESG rating %s from %.1f to %.1f", changeType, previousScore, newScore),
                "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.info("Rating change notification sent for company: {} ({} -> {})",
                company.getCompanyId(), previousScore, newScore);
    }

    public void sendRankingChangeNotification(String companyId, String companyName,
                                              Integer previousRank, Integer newRank) {
        Map<String, Object> notification = Map.of(
                "type", "RANKING_CHANGE",
                "companyId", companyId,
                "companyName", companyName,
                "previousRank", previousRank,
                "newRank", newRank,
                "message", String.format("Ranking changed from %d to %d", previousRank, newRank),
                "timestamp", System.currentTimeMillis()
        );

        messagingTemplate.convertAndSend("/topic/notifications", notification);
        log.debug("Ranking change notification sent for company: {} ({} -> {})",
                companyId, previousRank, newRank);
    }
}