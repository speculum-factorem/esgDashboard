package com.esg.dashboard.event;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Публикатор событий для ESG Dashboard
 * Используется для развязки компонентов через Spring Events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishCompanyUpdated(Company company, String action) {
        try {
            MDC.put("companyId", company.getCompanyId());
            MDC.put("operation", "PUBLISH_COMPANY_UPDATED");
            MDC.put("action", action);
            log.debug("Publishing company update event: {}, action: {}", company.getCompanyId(), action);
            eventPublisher.publishEvent(new CompanyUpdatedEvent(this, company, action));
        } finally {
            MDC.clear();
        }
    }

    public void publishRatingUpdated(String companyId, ESGRating previousRating, ESGRating newRating) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "PUBLISH_RATING_UPDATED");
            log.debug("Publishing rating update event for company: {}", companyId);
            eventPublisher.publishEvent(new RatingUpdatedEvent(this, companyId, previousRating, newRating));
        } finally {
            MDC.clear();
        }
    }
}

