package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.ExternalESGDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/external")
@RequiredArgsConstructor
public class ExternalDataController {

    private final ExternalESGDataService externalESGDataService;

    @GetMapping("/companies/{companyId}/esg")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCompanyESGData(
            @PathVariable String companyId) {

        log.info("Fetching external ESG data for company: {}", companyId);

        Map<String, Object> esgData = externalESGDataService.fetchCompanyESGData(companyId);
        return ResponseEntity.ok(ApiResponse.success(esgData));
    }

    @PostMapping("/companies/{companyId}/sync")
    public ResponseEntity<ApiResponse<String>> syncCompanyData(@PathVariable String companyId) {
        log.info("Syncing external data for company: {}", companyId);

        externalESGDataService.syncCompanyData(companyId);
        return ResponseEntity.ok(ApiResponse.success("Sync initiated"));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getExternalApiStatus() {
        boolean configured = externalESGDataService.isExternalApiConfigured();

        Map<String, Object> status = Map.of(
                "externalApiConfigured", configured,
                "timestamp", java.time.LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(ApiResponse.success(status));
    }
}