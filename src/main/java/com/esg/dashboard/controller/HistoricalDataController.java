package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.model.HistoricalData;
import com.esg.dashboard.service.HistoricalDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
@Tag(name = "Исторические данные", description = "API для получения исторических ESG данных компаний")
public class HistoricalDataController {

    private final HistoricalDataService historicalDataService;

    @GetMapping("/company/{companyId}")
    @Operation(
            summary = "Получение истории компании",
            description = "Возвращает исторические данные ESG показателей для указанной компании с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Исторические данные успешно получены")
    })
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<HistoricalData>>> getCompanyHistory(
            @Parameter(description = "Идентификатор компании", required = true)
            @PathVariable String companyId,
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") 
            @jakarta.validation.constraints.Min(1) 
            @jakarta.validation.constraints.Max(100) 
            int size) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_COMPANY_HISTORY");
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching historical data for company: {} - page: {}, size: {}", companyId, page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<HistoricalData> history = historicalDataService.getCompanyHistory(companyId, pageable);
            log.debug("Found {} historical records for company {} (page {})", history.getContent().size(), companyId, page);
            return ResponseEntity.ok(ApiResponse.success(history));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/company/{companyId}/type/{dataType}")
    @Operation(
            summary = "Получение исторических данных по типу",
            description = "Возвращает исторические данные определенного типа за указанный период с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Данные успешно получены")
    })
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<HistoricalData>>> getHistoricalDataByType(
            @Parameter(description = "Идентификатор компании", required = true)
            @PathVariable String companyId,
            @Parameter(description = "Тип данных (rating, carbon, social)", required = true)
            @PathVariable String dataType,
            @Parameter(description = "Начальная дата (ISO format)", required = true, example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "Конечная дата (ISO format)", required = true, example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") 
            @jakarta.validation.constraints.Min(1) 
            @jakarta.validation.constraints.Max(100) 
            int size) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_HISTORICAL_DATA_BY_TYPE");
            MDC.put("dataType", dataType);
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching historical data for company: {}, type: {}, period: {} - {} - page: {}, size: {}", 
                    companyId, dataType, from, to, page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<HistoricalData> history = historicalDataService.getHistoricalData(companyId, dataType, from, to, pageable);
            log.debug("Found {} records for type {} in the specified period (page {})", history.getContent().size(), dataType, page);
            return ResponseEntity.ok(ApiResponse.success(history));
        } finally {
            MDC.clear();
        }
    }
}