package com.esg.dashboard.controller;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Компании", description = "API для управления компаниями и ESG рейтингами")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @Operation(
            summary = "Создание новой компании",
            description = "Создает новую компанию с ESG показателями. Обновления публикуются в реальном времени через WebSocket."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Компания успешно создана",
                    content = @Content(schema = @Schema(implementation = Company.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Company> createCompany(
            @Parameter(description = "Данные компании", required = true)
            @Valid @RequestBody Company company) {
        try {
            MDC.put("companyId", company.getCompanyId());
            MDC.put("operation", "CREATE_COMPANY");
            log.info("Creating new company: {}", company.getName());

            Company savedCompany = companyService.saveOrUpdateCompany(company);
            // Обновления в реальном времени публикуются через слушатель событий

            log.info("Company successfully created with ID: {}", savedCompany.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCompany);
        } catch (Exception e) {
            log.error("Error creating company: {}", e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{companyId}")
    @Operation(
            summary = "Получение компании по ID",
            description = "Возвращает информацию о компании по её уникальному идентификатору"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Компания найдена",
                    content = @Content(schema = @Schema(implementation = Company.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Компания не найдена")
    })
    public ResponseEntity<Company> getCompany(
            @Parameter(description = "Идентификатор компании", required = true, example = "COMP001")
            @PathVariable String companyId) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "GET_COMPANY");
            log.info("Fetching company by ID: {}", companyId);

            return companyService.findByCompanyId(companyId)
                    .map(company -> {
                        log.debug("Company found: {}", company.getName());
                        return ResponseEntity.ok(company);
                    })
                    .orElseGet(() -> {
                        log.warn("Company not found: {}", companyId);
                        return ResponseEntity.notFound().build();
                    });
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{companyId}/rating")
    @Operation(
            summary = "Обновление ESG рейтинга компании",
            description = "Обновляет ESG рейтинг компании. Изменения публикуются в реальном времени."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Рейтинг успешно обновлен",
                    content = @Content(schema = @Schema(implementation = Company.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Компания не найдена")
    })
    public ResponseEntity<Company> updateRating(
            @Parameter(description = "Идентификатор компании", required = true)
            @PathVariable String companyId,
            @Parameter(description = "Новый ESG рейтинг", required = true)
            @Valid @RequestBody ESGRating newRating) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "UPDATE_RATING");
            log.info("Updating rating for company: {}", companyId);

            companyService.updateESGRating(companyId, newRating);
            // Обновления в реальном времени публикуются через слушатель событий

            log.info("Rating successfully updated for company: {}", companyId);
            return getCompany(companyId);
        } catch (IllegalArgumentException e) {
            log.error("Company not found for rating update: {}", companyId);
            return ResponseEntity.notFound().build();
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/top-ranked")
    @Operation(
            summary = "Получение топ компаний по рейтингу",
            description = "Возвращает список компаний с наивысшими ESG рейтингами с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список компаний успешно получен")
    })
    public ResponseEntity<org.springframework.data.domain.Page<Company>> getTopRankedCompanies(
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") 
            @jakarta.validation.constraints.Min(1) 
            @jakarta.validation.constraints.Max(100) 
            int size) {
        try {
            MDC.put("operation", "GET_TOP_RANKED");
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching top ranked companies - page: {}, size: {}", page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<Company> topCompanies = companyService.getTopRankedCompanies(pageable);
            log.debug("Found {} companies in top ranking (page {})", topCompanies.getContent().size(), page);
            return ResponseEntity.ok(topCompanies);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/sector/{sector}")
    @Operation(
            summary = "Получение компаний по сектору",
            description = "Возвращает список всех компаний в указанном секторе с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список компаний успешно получен")
    })
    public ResponseEntity<org.springframework.data.domain.Page<Company>> getCompaniesBySector(
            @Parameter(description = "Название сектора", required = true, example = "Technology")
            @PathVariable String sector,
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") 
            @jakarta.validation.constraints.Min(1) 
            @jakarta.validation.constraints.Max(100) 
            int size) {
        try {
            MDC.put("operation", "GET_BY_SECTOR");
            MDC.put("sector", sector);
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching companies in sector: {} - page: {}, size: {}", sector, page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<Company> companies = companyService.getCompaniesBySector(sector, pageable);
            log.debug("Found {} companies in sector {} (page {})", companies.getContent().size(), sector, page);
            return ResponseEntity.ok(companies);
        } finally {
            MDC.clear();
        }
    }
}