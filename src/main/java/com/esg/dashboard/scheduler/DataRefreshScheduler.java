package com.esg.dashboard.scheduler;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Планировщик задач для периодического обновления данных
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataRefreshScheduler {

    private final CompanyService companyService;

    /**
     * Обновление рейтингов компаний каждые 5 минут
     */
    @Scheduled(fixedRate = 300000) // 5 минут
    public void refreshCompanyRankings() {
        try {
            MDC.put("operation", "REFRESH_COMPANY_RANKINGS");
            log.info("Starting scheduled company rankings refresh");

            // Получаем топ компаний для прогрева кэша
            List<Company> topCompanies = companyService.getTopRankedCompanies(50);
            log.debug("Rankings refreshed for {} companies", topCompanies.size());

        } catch (Exception e) {
            log.error("Error refreshing company rankings: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Очистка старых записей кэша каждый час
     */
    @Scheduled(fixedRate = 3600000) // 1 час
    public void cleanupOldData() {
        try {
            MDC.put("operation", "CLEANUP_OLD_DATA");
            log.info("Starting scheduled old data cleanup");
            // Реализация очистки старых данных будет здесь
            log.debug("Old data cleanup completed");
        } catch (Exception e) {
            log.error("Error cleaning up old data: {}", e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}