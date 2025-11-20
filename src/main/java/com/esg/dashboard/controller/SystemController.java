package com.esg.dashboard.controller;

import com.esg.dashboard.cache.CacheManagerService;
import com.esg.dashboard.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemController {

    private final CacheManagerService cacheManagerService;

    @PostMapping("/cache/clear")
    public ResponseEntity<ApiResponse<String>> clearCache() {
        log.info("Clearing all cache");

        cacheManagerService.clearAllCache();
        return ResponseEntity.ok(ApiResponse.success("Cache cleared successfully"));
    }

    @PostMapping("/cache/companies/{companyId}/evict")
    public ResponseEntity<ApiResponse<String>> evictCompanyCache(@PathVariable String companyId) {
        log.info("Evicting cache for company: {}", companyId);

        cacheManagerService.evictCompanyCache(companyId);
        return ResponseEntity.ok(ApiResponse.success("Company cache evicted"));
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCacheStats() {
        log.debug("Getting cache statistics");

        var stats = cacheManagerService.getCacheStats();
        Map<String, Object> statsMap = Map.of(
                "totalKeys", stats.getTotalKeys() != null ? stats.getTotalKeys() : 0,
                "companyKeys", stats.getCompanyKeys() != null ? stats.getCompanyKeys() : 0,
                "portfolioKeys", stats.getPortfolioKeys() != null ? stats.getPortfolioKeys() : 0
        );

        return ResponseEntity.ok(ApiResponse.success(statsMap));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSystemInfo() {
        Map<String, String> info = Map.of(
                "name", "ESG Dashboard",
                "version", "1.0.0",
                "javaVersion", System.getProperty("java.version"),
                "availableProcessors", String.valueOf(Runtime.getRuntime().availableProcessors()),
                "maxMemory", String.valueOf(Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB",
                "timestamp", java.time.LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(ApiResponse.success(info));
    }
}