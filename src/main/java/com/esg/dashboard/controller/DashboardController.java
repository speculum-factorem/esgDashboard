package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.DashboardMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardMetricsService dashboardMetricsService;

    @GetMapping("/metrics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardMetrics() {
        log.debug("Fetching dashboard metrics");

        Map<String, Object> metrics = dashboardMetricsService.getOverallMetrics();
        return ResponseEntity.ok(ApiResponse.success(metrics));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        log.debug("Health check endpoint called");

        Map<String, String> healthStatus = Map.of(
                "status", "UP",
                "timestamp", java.time.LocalDateTime.now().toString(),
                "service", "ESG Dashboard API"
        );

        return ResponseEntity.ok(ApiResponse.success(healthStatus));
    }
}