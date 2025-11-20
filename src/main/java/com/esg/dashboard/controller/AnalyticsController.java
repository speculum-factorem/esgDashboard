package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/sectors")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSectorAnalytics() {
        log.debug("Fetching sector analytics");

        Map<String, Object> analytics = analyticsService.getSectorAnalytics();
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrendAnalytics(
            @RequestParam(defaultValue = "30d") String period) {
        log.debug("Fetching trend analytics for period: {}", period);

        Map<String, Object> trends = analyticsService.getTrendAnalytics(period);
        return ResponseEntity.ok(ApiResponse.success(trends));
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        log.debug("Fetching performance metrics");

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

        return ResponseEntity.ok(ApiResponse.success(metrics));
    }
}