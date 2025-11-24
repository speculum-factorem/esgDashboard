package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.DashboardMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Дашборд", description = "API для получения метрик и состояния дашборда")
public class DashboardController {

    private final DashboardMetricsService dashboardMetricsService;

    @GetMapping("/metrics")
    @Operation(
            summary = "Получение метрик дашборда",
            description = "Возвращает общие метрики ESG Dashboard включая количество компаний, средние показатели и распределения"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Метрики успешно получены")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardMetrics() {
        try {
            MDC.put("operation", "GET_DASHBOARD_METRICS");
            log.info("Fetching dashboard metrics");

            Map<String, Object> metrics = dashboardMetricsService.getOverallMetrics();
            log.debug("Dashboard metrics successfully retrieved");
            return ResponseEntity.ok(ApiResponse.success(metrics));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/health")
    @Operation(
            summary = "Проверка здоровья сервиса",
            description = "Возвращает статус работы ESG Dashboard API"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Сервис работает")
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        try {
            MDC.put("operation", "HEALTH_CHECK");
            log.debug("Health check endpoint called");

            Map<String, String> healthStatus = Map.of(
                    "status", "UP",
                    "timestamp", java.time.LocalDateTime.now().toString(),
                    "service", "ESG Dashboard API"
            );

            return ResponseEntity.ok(ApiResponse.success(healthStatus));
        } finally {
            MDC.clear();
        }
    }
}