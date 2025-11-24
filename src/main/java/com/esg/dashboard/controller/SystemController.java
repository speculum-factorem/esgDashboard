package com.esg.dashboard.controller;

import com.esg.dashboard.cache.CacheManagerService;
import com.esg.dashboard.dto.ApiResponse;
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
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Tag(name = "Система", description = "API для управления системой и кэшем")
public class SystemController {

    private final CacheManagerService cacheManagerService;

    @PostMapping("/cache/clear")
    @Operation(
            summary = "Очистка всего кэша",
            description = "Очищает весь кэш Redis. Используйте с осторожностью в production."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Кэш успешно очищен")
    })
    public ResponseEntity<ApiResponse<String>> clearCache() {
        try {
            MDC.put("operation", "CLEAR_CACHE");
            log.info("Clearing all cache");

            cacheManagerService.clearAllCache();
            log.info("All cache successfully cleared");
            return ResponseEntity.ok(ApiResponse.success("Cache cleared successfully"));
        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/cache/companies/{companyId}/evict")
    @Operation(
            summary = "Удаление компании из кэша",
            description = "Удаляет данные конкретной компании из кэша"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Кэш компании успешно очищен")
    })
    public ResponseEntity<ApiResponse<String>> evictCompanyCache(
            @Parameter(description = "Идентификатор компании", required = true)
            @PathVariable String companyId) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "EVICT_COMPANY_CACHE");
            log.info("Evicting cache for company: {}", companyId);

            cacheManagerService.evictCompanyCache(companyId);
            log.debug("Company cache successfully evicted: {}", companyId);
            return ResponseEntity.ok(ApiResponse.success("Company cache evicted"));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/cache/stats")
    @Operation(
            summary = "Статистика кэша",
            description = "Возвращает статистику использования кэша Redis"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Статистика успешно получена")
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats() {
        try {
            MDC.put("operation", "GET_CACHE_STATS");
            log.debug("Getting cache statistics");

            var stats = cacheManagerService.getCacheStats();
            Map<String, Object> statsMap = Map.of(
                    "totalKeys", stats.getTotalKeys() != null ? stats.getTotalKeys() : 0,
                    "companyKeys", stats.getCompanyKeys() != null ? stats.getCompanyKeys() : 0,
                    "portfolioKeys", stats.getPortfolioKeys() != null ? stats.getPortfolioKeys() : 0
            );

            log.debug("Cache statistics retrieved: total keys {}", statsMap.get("totalKeys"));
            return ResponseEntity.ok(ApiResponse.success(statsMap));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/info")
    @Operation(
            summary = "Информация о системе",
            description = "Возвращает информацию о версии системы и окружении"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Информация успешно получена")
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> getSystemInfo() {
        try {
            MDC.put("operation", "GET_SYSTEM_INFO");
            log.debug("Getting system information");

            Map<String, String> info = Map.of(
                    "name", "ESG Dashboard",
                    "version", "1.0.0",
                    "javaVersion", System.getProperty("java.version"),
                    "availableProcessors", String.valueOf(Runtime.getRuntime().availableProcessors()),
                    "maxMemory", String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB",
                    "timestamp", java.time.LocalDateTime.now().toString()
            );

            return ResponseEntity.ok(ApiResponse.success(info));
        } finally {
            MDC.clear();
        }
    }
}