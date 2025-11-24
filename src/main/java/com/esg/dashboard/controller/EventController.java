package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.model.ESGUpdateEvent;
import com.esg.dashboard.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "События", description = "API для получения истории событий ESG обновлений")
public class EventController {

    private final EventService eventService;

    @GetMapping
    @Operation(
            summary = "Получение последних событий",
            description = "Возвращает список последних ESG событий в системе с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "События успешно получены")
    })
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ESGUpdateEvent>>> getRecentEvents(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") 
            @jakarta.validation.constraints.Min(1) 
            @jakarta.validation.constraints.Max(100) 
            int size) {
        try {
            MDC.put("operation", "GET_RECENT_EVENTS");
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching recent events - page: {}, size: {}", page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<ESGUpdateEvent> events = eventService.getRecentEvents(pageable);
            log.debug("Found {} events (page {})", events.getContent().size(), page);
            return ResponseEntity.ok(ApiResponse.success(events));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/company/{companyId}")
    @Operation(
            summary = "Получение событий компании",
            description = "Возвращает список событий ESG обновлений для конкретной компании с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "События успешно получены")
    })
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<ESGUpdateEvent>>> getCompanyEvents(
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
            MDC.put("operation", "GET_COMPANY_EVENTS");
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching events for company: {} - page: {}, size: {}", companyId, page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<ESGUpdateEvent> events = eventService.getCompanyEvents(companyId, pageable);
            log.debug("Found {} events for company {} (page {})", events.getContent().size(), companyId, page);
            return ResponseEntity.ok(ApiResponse.success(events));
        } finally {
            MDC.clear();
        }
    }
}