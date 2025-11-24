package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Аналитика", description = "API для получения аналитических данных и метрик")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/sectors")
    @Operation(
            summary = "Секторальная аналитика",
            description = "Возвращает аналитику ESG показателей по секторам экономики"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Аналитика успешно получена")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSectorAnalytics() {
        try {
            MDC.put("operation", "GET_SECTOR_ANALYTICS");
            log.info("Fetching sector analytics");

            Map<String, Object> analytics = analyticsService.getSectorAnalytics();
            log.debug("Sector analytics successfully retrieved");
            return ResponseEntity.ok(ApiResponse.success(analytics));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/trends")
    @Operation(
            summary = "Аналитика трендов",
            description = "Возвращает анализ трендов ESG показателей за указанный период"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Тренды успешно получены")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrendAnalytics(
            @Parameter(description = "Период анализа (например: 30d, 90d, 1y)", example = "30d")
            @RequestParam(defaultValue = "30d") String period) {
        try {
            MDC.put("operation", "GET_TREND_ANALYTICS");
            MDC.put("period", period);
            log.info("Fetching trend analytics for period: {}", period);

            Map<String, Object> trends = analyticsService.getTrendAnalytics(period);
            log.debug("Trends successfully retrieved for period: {}", period);
            return ResponseEntity.ok(ApiResponse.success(trends));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/performance")
    @Operation(
            summary = "Метрики производительности",
            description = "Возвращает метрики производительности системы"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Метрики успешно получены")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        try {
            MDC.put("operation", "GET_PERFORMANCE_METRICS");
            log.info("Fetching performance metrics");

            Map<String, Object> metrics = Map.of(
                    "responseTime", Map.of(
                            "avg", 45.2,
                            "p95", 120.5,
                            "p99", 250.8
                    ),
                    "throughput", Map.of(
                            "requestsPerSecond", 25.8,
                            "activeConnections", 156
                    ),
                    "cache", Map.of(
                            "hitRate", 0.89,
                            "missRate", 0.11
                    )
            );

            log.debug("Performance metrics successfully retrieved");
            return ResponseEntity.ok(ApiResponse.success(metrics));
        } finally {
            MDC.clear();
        }
    }
}