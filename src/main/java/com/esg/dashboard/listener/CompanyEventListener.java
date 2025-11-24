package com.esg.dashboard.listener;

import com.esg.dashboard.event.CompanyUpdatedEvent;
import com.esg.dashboard.event.RatingUpdatedEvent;
import com.esg.dashboard.service.RealTimeUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Слушатель событий обновления компаний
 * Обрабатывает события асинхронно и публикует обновления в реальном времени
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CompanyEventListener {

    private final RealTimeUpdateService realTimeUpdateService;

    @Async
    @EventListener
    public void handleCompanyUpdated(CompanyUpdatedEvent event) {
        try {
            MDC.put("companyId", event.getCompany().getCompanyId());
            MDC.put("operation", "HANDLE_COMPANY_UPDATED");
            MDC.put("action", event.getAction());
            log.info("Handling company update event: {}, action: {}", 
                    event.getCompany().getCompanyId(), event.getAction());
            
            // Публикуем обновление в реальном времени
            realTimeUpdateService.publishCompanyUpdate(event.getCompany());
            log.debug("Company update successfully published");
        } finally {
            MDC.clear();
        }
    }

    @Async
    @EventListener
    public void handleRatingUpdated(RatingUpdatedEvent event) {
        try {
            MDC.put("companyId", event.getCompanyId());
            MDC.put("operation", "HANDLE_RATING_UPDATED");
            log.info("Handling rating update event for company: {}", event.getCompanyId());
            
            // Публикуем обновление рейтинга в реальном времени
            realTimeUpdateService.publishRatingUpdate(event.getCompanyId(), event.getNewRating());
            log.debug("Rating update successfully published");
        } finally {
            MDC.clear();
        }
    }
}

