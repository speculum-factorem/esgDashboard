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
        try {
            org.slf4j.MDC.put("operation", "GET_OVERALL_METRICS");
            log.info("Calculating overall dashboard metrics");

            Map<String, Object> metrics = new HashMap<>();

            // Общее количество компаний
            long totalCompanies = mongoTemplate.getCollection("companies").countDocuments();
            metrics.put("totalCompanies", totalCompanies);

            // Средний ESG счет
            Double averageEsgScore = calculateAverageEsgScore();
            metrics.put("averageEsgScore", averageEsgScore);

            // Распределение по секторам
            Map<String, Long> sectorDistribution = calculateSectorDistribution();
            metrics.put("sectorDistribution", sectorDistribution);

            // Топ-компания
            String topPerformer = getTopPerformer();
            metrics.put("topPerformer", topPerformer);

            // Тренд углеродного следа (моковые данные - в реальном приложении сравнивались бы с историческими)
            metrics.put("carbonFootprintTrend", -12.5);

            // Распределение по рейтингам
            Map<String, Long> ratingDistribution = calculateRatingDistribution();
            metrics.put("ratingDistribution", ratingDistribution);

            log.info("Dashboard metrics successfully calculated");
            return metrics;

        } catch (Exception e) {
            log.error("Error calculating dashboard metrics: {}", e.getMessage(), e);
            // Возвращаем метрики по умолчанию в случае ошибки
            return getDefaultMetrics();
        } finally {
            org.slf4j.MDC.clear();
        }
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
        // Получаем топ-компанию из рейтинга Redis
        var topCompanies = companyService.getTopRankedCompanies(1);
        if (!topCompanies.isEmpty()) {
            return topCompanies.get(0).getName();
        }
        return "N/A";
    }

    private Map<String, Long> calculateRatingDistribution() {
        // Моковая реализация - в реальном приложении агрегировались бы данные из коллекции companies
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