package com.esg.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;

    public Map<String, Object> getSectorAnalytics() {
        try {
            MDC.put("operation", "GET_SECTOR_ANALYTICS");
            log.info("Calculating sector analytics");

            // Агрегация данных по секторам для расчета средних показателей
            Aggregation aggregation = newAggregation(
                    group("sector")
                            .avg("currentRating.overallScore").as("avgScore")
                            .avg("currentRating.environmentalScore").as("avgEnvironmental")
                            .avg("currentRating.socialScore").as("avgSocial")
                            .avg("currentRating.governanceScore").as("avgGovernance")
                            .count().as("companyCount")
            );

            AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "companies", Map.class);

            Map<String, Object> analytics = new HashMap<>();
            analytics.put("sectorBreakdown", results.getMappedResults());
            analytics.put("totalSectors", results.getMappedResults().size());

            log.info("Sector analytics calculated for {} sectors", results.getMappedResults().size());
            return analytics;

        } catch (Exception e) {
            log.error("Error calculating sector analytics: {}", e.getMessage(), e);
            return Map.of("error", "Failed to calculate analytics");
        } finally {
            MDC.clear();
        }
    }

    public Map<String, Object> getTrendAnalytics(String period) {
        try {
            MDC.put("operation", "GET_TREND_ANALYTICS");
            MDC.put("period", period);
            log.info("Calculating trend analytics for period: {}", period);

            // Моковая реализация - в реальном приложении агрегировались бы исторические данные
            Map<String, Object> trends = Map.of(
                    "period", period,
                    "esgTrend", 2.5,
                    "carbonReduction", -8.2,
                    "socialImprovement", 3.1,
                    "totalUpdates", 156
            );
            
            log.debug("Trend analytics successfully calculated");
            return trends;
        } finally {
            MDC.clear();
        }
    }
}