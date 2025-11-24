package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.model.Portfolio;
import com.esg.dashboard.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
@Tag(name = "Портфели", description = "API для управления инвестиционными портфелями")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    @Operation(
            summary = "Создание нового портфеля",
            description = "Создает новый инвестиционный портфель с автоматическим расчетом агрегированных ESG показателей"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Портфель успешно создан"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<ApiResponse<Portfolio>> createPortfolio(
            @Parameter(description = "Данные портфеля", required = true)
            @Valid @RequestBody Portfolio portfolio) {
        try {
            MDC.put("portfolioId", portfolio.getPortfolioId());
            MDC.put("clientId", portfolio.getClientId());
            MDC.put("operation", "CREATE_PORTFOLIO");
            log.info("Creating portfolio for client: {}", portfolio.getClientId());

            Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio);
            log.info("Portfolio successfully created with ID: {}", createdPortfolio.getPortfolioId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdPortfolio));
        } catch (Exception e) {
            log.error("Error creating portfolio: {}", e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{portfolioId}")
    @Operation(
            summary = "Получение портфеля по ID",
            description = "Возвращает информацию о портфеле по его уникальному идентификатору"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Портфель найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Портфель не найден")
    })
    public ResponseEntity<ApiResponse<Portfolio>> getPortfolio(
            @Parameter(description = "Идентификатор портфеля", required = true)
            @PathVariable String portfolioId) {
        try {
            MDC.put("portfolioId", portfolioId);
            MDC.put("operation", "GET_PORTFOLIO");
            log.info("Fetching portfolio by ID: {}", portfolioId);

            return portfolioService.findByPortfolioId(portfolioId)
                    .map(portfolio -> {
                        log.debug("Portfolio found: {}", portfolio.getPortfolioName());
                        return ResponseEntity.ok(ApiResponse.success(portfolio));
                    })
                    .orElseGet(() -> {
                        log.warn("Portfolio not found: {}", portfolioId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error("Portfolio not found"));
                    });
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/client/{clientId}")
    @Operation(
            summary = "Получение портфелей клиента",
            description = "Возвращает список всех портфелей для указанного клиента с поддержкой пагинации"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Список портфелей успешно получен")
    })
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<Portfolio>>> getClientPortfolios(
            @Parameter(description = "Идентификатор клиента", required = true)
            @PathVariable String clientId,
            @Parameter(description = "Номер страницы (начиная с 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") 
            @jakarta.validation.constraints.Min(1) 
            @jakarta.validation.constraints.Max(100) 
            int size) {
        try {
            MDC.put("clientId", clientId);
            MDC.put("operation", "GET_CLIENT_PORTFOLIOS");
            MDC.put("page", String.valueOf(page));
            MDC.put("size", String.valueOf(size));
            log.info("Fetching portfolios for client: {} - page: {}, size: {}", clientId, page, size);

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<Portfolio> portfolios = portfolioService.findByClientId(clientId, pageable);
            log.debug("Found {} portfolios for client {} (page {})", portfolios.getContent().size(), clientId, page);
            return ResponseEntity.ok(ApiResponse.success(portfolios));
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{portfolioId}")
    @Operation(
            summary = "Обновление портфеля",
            description = "Обновляет существующий портфель и пересчитывает агрегированные ESG показатели"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Портфель успешно обновлен"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Портфель не найден"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    public ResponseEntity<ApiResponse<Portfolio>> updatePortfolio(
            @Parameter(description = "Идентификатор портфеля", required = true)
            @PathVariable String portfolioId,
            @Parameter(description = "Обновленные данные портфеля", required = true)
            @Valid @RequestBody Portfolio portfolioUpdate) {
        try {
            MDC.put("portfolioId", portfolioId);
            MDC.put("operation", "UPDATE_PORTFOLIO");
            log.info("Updating portfolio: {}", portfolioId);

            Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolioId, portfolioUpdate);
            log.info("Portfolio successfully updated: {}", portfolioId);
            return ResponseEntity.ok(ApiResponse.success(updatedPortfolio));
        } catch (IllegalArgumentException e) {
            log.warn("Portfolio not found for update: {}", portfolioId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{portfolioId}")
    @Operation(
            summary = "Удаление портфеля",
            description = "Удаляет портфель по его идентификатору"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Портфель успешно удален"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Портфель не найден")
    })
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @Parameter(description = "Идентификатор портфеля", required = true)
            @PathVariable String portfolioId) {
        try {
            MDC.put("portfolioId", portfolioId);
            MDC.put("operation", "DELETE_PORTFOLIO");
            log.info("Deleting portfolio: {}", portfolioId);

            portfolioService.deletePortfolio(portfolioId);
            log.info("Portfolio successfully deleted: {}", portfolioId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            log.warn("Portfolio not found for deletion: {}", portfolioId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } finally {
            MDC.clear();
        }
    }
}