package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.service.DataExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
@Tag(name = "Экспорт данных", description = "API для экспорта данных в различных форматах")
public class ExportController {

    private final DataExportService dataExportService;

    @GetMapping("/companies/json")
    @Operation(
            summary = "Экспорт компаний в JSON",
            description = "Экспортирует все компании в формате JSON"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Экспорт успешно выполнен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка экспорта")
    })
    public ResponseEntity<ApiResponse<String>> exportCompaniesJson() {
        try {
            MDC.put("operation", "EXPORT_COMPANIES_JSON");
            log.info("Exporting companies data as JSON");

            String jsonData = dataExportService.exportCompaniesToJson();
            log.debug("JSON export completed successfully, data size: {} bytes", jsonData.length());
            return ResponseEntity.ok(ApiResponse.success(jsonData));
        } catch (Exception e) {
            log.error("Error exporting JSON: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Export failed: " + e.getMessage()));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/companies/csv")
    @Operation(
            summary = "Экспорт компаний в CSV",
            description = "Экспортирует все компании в формате CSV с автоматической загрузкой файла"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSV файл успешно сгенерирован"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка экспорта")
    })
    public ResponseEntity<byte[]> exportCompaniesCsv() {
        try {
            MDC.put("operation", "EXPORT_COMPANIES_CSV");
            log.info("Exporting companies data as CSV");

            byte[] csvData = dataExportService.exportCompaniesToCsv();
            String filename = dataExportService.generateExportFilename("companies");

            log.debug("CSV file generated successfully: {}, size: {} bytes", filename, csvData.length);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(csvData);

        } catch (Exception e) {
            log.error("Error exporting CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/portfolios/json")
    @Operation(
            summary = "Экспорт портфелей в JSON",
            description = "Экспортирует все портфели в формате JSON"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Экспорт успешно выполнен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Ошибка экспорта")
    })
    public ResponseEntity<ApiResponse<String>> exportPortfoliosJson() {
        try {
            MDC.put("operation", "EXPORT_PORTFOLIOS_JSON");
            log.info("Exporting portfolios data as JSON");

            String jsonData = dataExportService.exportPortfoliosToJson();
            log.debug("Portfolios JSON export completed successfully, data size: {} bytes", jsonData.length());
            return ResponseEntity.ok(ApiResponse.success(jsonData));
        } catch (Exception e) {
            log.error("Error exporting portfolios JSON: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Export failed: " + e.getMessage()));
        } finally {
            MDC.clear();
        }
    }
}