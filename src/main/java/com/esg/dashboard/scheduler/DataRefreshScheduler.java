package com.esg.dashboard.scheduler;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataRefreshScheduler {

    private final CompanyService companyService;

    /**
     * Refresh company rankings every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void refreshCompanyRankings() {
        log.info("Starting scheduled company rankings refresh");

        try {
            // Get top companies to ensure cache is warm
            List<Company> topCompanies = companyService.getTopRankedCompanies(50);
            log.debug("Refreshed rankings for {} companies", topCompanies.size());

        } catch (Exception e) {
            log.error("Error refreshing company rankings: {}", e.getMessage());
        }
    }

    /**
     * Clean up old cache entries every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupOldData() {
        log.info("Starting scheduled data cleanup");
        // Implementation for cleaning up old data would go here
    }
}