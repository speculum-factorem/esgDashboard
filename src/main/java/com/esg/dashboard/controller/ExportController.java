package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.DataExportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final DataExportService dataExportService;

    @GetMapping("/companies/json")
    public ResponseEntity<ApiResponse<String>> exportCompaniesJson() {
        log.info("Exporting companies data as JSON");

        try {
            String jsonData = dataExportService.exportCompaniesToJson();
            return ResponseEntity.ok(ApiResponse.success(jsonData));
        } catch (Exception e) {
            log.error("JSON export failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Export failed: " + e.getMessage()));
        }
    }

    @GetMapping("/companies/csv")
    public ResponseEntity<byte[]> exportCompaniesCsv() {
        log.info("Exporting companies data as CSV");

        try {
            byte[] csvData = dataExportService.exportCompaniesToCsv();
            String filename = dataExportService.generateExportFilename("companies");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvData);

        } catch (Exception e) {
            log.error("CSV export failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/portfolios/json")
    public ResponseEntity<ApiResponse<String>> exportPortfoliosJson() {
        log.info("Exporting portfolios data as JSON");

        try {
            String jsonData = dataExportService.exportPortfoliosToJson();
            return ResponseEntity.ok(ApiResponse.success(jsonData));
        } catch (Exception e) {
            log.error("Portfolios JSON export failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Export failed: " + e.getMessage()));
        }
    }
}