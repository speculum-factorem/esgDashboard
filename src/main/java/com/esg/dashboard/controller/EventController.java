package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.model.ESGUpdateEvent;
import com.esg.dashboard.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ESGUpdateEvent>>> getRecentEvents(
            @RequestParam(defaultValue = "50") int limit) {
        log.debug("Fetching recent events, limit: {}", limit);

        List<ESGUpdateEvent> events = eventService.getRecentEvents(limit);
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<List<ESGUpdateEvent>>> getCompanyEvents(
            @PathVariable String companyId,
            @RequestParam(defaultValue = "20") int limit) {
        log.debug("Fetching events for company: {}, limit: {}", companyId, limit);

        List<ESGUpdateEvent> events = eventService.getCompanyEvents(companyId, limit);
        return ResponseEntity.ok(ApiResponse.success(events));
    }
}