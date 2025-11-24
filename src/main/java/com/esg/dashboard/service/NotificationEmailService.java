package com.esg.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Сервис для отправки email уведомлений
 * В реальной реализации интегрируется с email сервисом (например, SendGrid, AWS SES)
 */
@Slf4j
@Service
public class NotificationEmailService {

    @Value("${app.notifications.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.notifications.email.recipient:admin@esg-dashboard.com}")
    private String defaultRecipient;

    public void sendRatingChangeNotification(String companyId, String companyName,
                                             Double previousScore, Double newScore) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "SEND_RATING_CHANGE_EMAIL");
            
            if (!emailEnabled) {
                log.debug("Email notifications are disabled");
                return;
            }

            String subject = String.format("ESG Rating Change - %s", companyName);
            double change = newScore - previousScore;
            String body = String.format(
                    "Company: %s (%s)\n" +
                            "Previous Score: %.2f\n" +
                            "New Score: %.2f\n" +
                            "Change: %.2f\n\n" +
                            "This is an automated notification from ESG Dashboard.",
                    companyName, companyId, previousScore, newScore, change
            );

            // В реальной реализации здесь будет интеграция с email сервисом
            // Пока просто логируем содержимое email
            log.info("Email Notification:\nTo: {}\nSubject: {}\nBody: {}",
                    defaultRecipient, subject, body);

        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    public void sendSystemAlert(String subject, String message, Map<String, Object> details) {
        try {
            MDC.put("operation", "SEND_SYSTEM_ALERT_EMAIL");
            
            if (!emailEnabled) {
                log.debug("Email notifications are disabled, system alert not sent");
                return;
            }

            String emailSubject = String.format("System Alert: %s", subject);
            String body = String.format(
                    "System Alert: %s\n\nMessage: %s\n\nDetails: %s\n\n" +
                            "This is an automated alert from ESG Dashboard.",
                    subject, message, details
            );

            log.info("System Alert Email:\nTo: {}\nSubject: {}\nBody: {}",
                    defaultRecipient, emailSubject, body);

        } catch (Exception e) {
            log.error("Failed to send system alert email: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}