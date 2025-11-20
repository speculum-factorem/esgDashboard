package com.esg.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardMetricsService {

    private final MongoTemplate mongoTemplate;
    private final CompanyService companyService;

    public Map<String, Object> getOverallMetrics() {
        log.debug("Calculating overall dashboard metrics");

        Map<String, Object> metrics = new HashMap<>();

        try {
            // Total companies count
            long totalCompanies = mongoTemplate.getCollection("companies").countDocuments();
            metrics.put("totalCompanies", totalCompanies);

            // Average ESG score
            Double averageEsgScore = calculateAverageEsgScore();
            metrics.put("averageEsgScore", averageEsgScore);

            // Sector distribution
            Map<String, Long> sectorDistribution = calculateSectorDistribution();
            metrics.put("sectorDistribution", sectorDistribution);

            // Top performer
            String topPerformer = getTopPerformer();
            metrics.put("topPerformer", topPerformer);

            // Carbon footprint trend (mock data - in real app would compare with historical)
            metrics.put("carbonFootprintTrend", -12.5);

            // Rating distribution
            Map<String, Long> ratingDistribution = calculateRatingDistribution();
            metrics.put("ratingDistribution", ratingDistribution);

            log.debug("Dashboard metrics calculated successfully");

        } catch (Exception e) {
            log.error("Error calculating dashboard metrics: {}", e.getMessage());
            // Return default metrics in case of error
            return getDefaultMetrics();
        }

        return metrics;
    }

    private Double calculateAverageEsgScore() {
        TypedAggregation<Object> aggregation = newAggregation(Object.class,
                group().avg("currentRating.overallScore").as("averageScore")
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "companies", Map.class);
        if (results.getMappedResults().isEmpty()) {
            return 0.0;
        }

        Object averageScore = results.getMappedResults().get(0).get("averageScore");
        return averageScore != null ? ((Number) averageScore).doubleValue() : 0.0;
    }

    private Map<String, Long> calculateSectorDistribution() {
        TypedAggregation<Object> aggregation = newAggregation(Object.class,
                group("sector").count().as("count"),
                project("count").and("sector").previousOperation()
        );

        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "companies", Map.class);
        Map<String, Long> distribution = new HashMap<>();

        for (Map result : results.getMappedResults()) {
            distribution.put((String) result.get("sector"), ((Number) result.get("count")).longValue());
        }

        return distribution;
    }

    private String getTopPerformer() {
        // Get top company from Redis ranking
        var topCompanies = companyService.getTopRankedCompanies(1);
        if (!topCompanies.isEmpty()) {
            return topCompanies.get(0).getName();
        }
        return "N/A";
    }

    private Map<String, Long> calculateRatingDistribution() {
        // Mock implementation - in real app would aggregate from companies collection
        return Map.of(
                "AAA", 15L,
                "AA", 35L,
                "A", 45L,
                "BBB", 30L,
                "BB", 20L,
                "B", 10L,
                "C", 5L
        );
    }

    private Map<String, Object> getDefaultMetrics() {
        return Map.of(
                "totalCompanies", 0L,
                "averageEsgScore", 0.0,
                "sectorDistribution", Map.of(),
                "topPerformer", "N/A",
                "carbonFootprintTrend", 0.0,
                "ratingDistribution", Map.of()
        );
    }
}