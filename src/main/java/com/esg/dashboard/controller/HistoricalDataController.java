package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.model.HistoricalData;
import com.esg.dashboard.service.HistoricalDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoricalDataController {

    private final HistoricalDataService historicalDataService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<HistoricalData>>> getCompanyHistory(
            @PathVariable String companyId,
            @RequestParam(defaultValue = "100") int limit) {

        log.debug("Fetching historical data for company: {}, limit: {}", companyId, limit);

        List<HistoricalData> history = historicalDataService.getCompanyHistory(companyId, limit);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/company/{companyId}/type/{dataType}")
    public ResponseEntity<ApiResponse<List<HistoricalData>>> getHistoricalDataByType(
            @PathVariable String companyId,
            @PathVariable String dataType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        log.debug("Fetching historical data for company: {}, type: {}, from: {}, to: {}",
                companyId, dataType, from, to);

        List<HistoricalData> history = historicalDataService.getHistoricalData(companyId, dataType, from, to);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}