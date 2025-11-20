package com.esg.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class NotificationEmailService {

    @Value("${app.notifications.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notifications.email.recipient:admin@esg-dashboard.com}")
    private String defaultRecipient;

    public void sendRatingChangeNotification(String companyId, String companyName,
                                             Double previousScore, Double newScore) {
        if (!emailEnabled) {
            log.debug("Email notifications are disabled");
            return;
        }

        try {
            String subject = String.format("ESG Rating Change - %s", companyName);
            String body = String.format(
                    "Company: %s (%s)\n" +
                            "Previous Score: %.2f\n" +
                            "New Score: %.2f\n" +
                            "Change: %.2f\n\n" +
                            "This is an automated notification from ESG Dashboard.",
                    companyName, companyId, previousScore, newScore, newScore - previousScore
            );

            // In a real implementation, this would integrate with an email service
            // For now, we'll just log the email content
            log.info("Email Notification:\nTo: {}\nSubject: {}\nBody: {}",
                    defaultRecipient, subject, body);

        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
        }
    }

    public void sendSystemAlert(String subject, String message, Map<String, Object> details) {
        if (!emailEnabled) {
            return;
        }

        try {
            String body = String.format(
                    "System Alert: %s\n\nMessage: %s\n\nDetails: %s\n\n" +
                            "This is an automated alert from ESG Dashboard.",
                    subject, message, details
            );

            log.info("System Alert Email:\nTo: {}\nSubject: {}\nBody: {}",
                    defaultRecipient, subject, body);

        } catch (Exception e) {
            log.error("Failed to send system alert email: {}", e.getMessage());
        }
    }
}