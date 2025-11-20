package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.model.Portfolio;
import com.esg.dashboard.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<ApiResponse<Portfolio>> createPortfolio(@Valid @RequestBody Portfolio portfolio) {
        try {
            MDC.put("portfolioId", portfolio.getPortfolioId());
            log.info("Creating portfolio for client: {}", portfolio.getClientId());

            Portfolio createdPortfolio = portfolioService.createPortfolio(portfolio);
            return ResponseEntity.ok(ApiResponse.success(createdPortfolio));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Portfolio>> getPortfolio(@PathVariable String portfolioId) {
        try {
            MDC.put("portfolioId", portfolioId);
            log.debug("Fetching portfolio: {}", portfolioId);

            return portfolioService.findByPortfolioId(portfolioId)
                    .map(portfolio -> ResponseEntity.ok(ApiResponse.success(portfolio)))
                    .orElse(ResponseEntity.ok(ApiResponse.error("Portfolio not found")));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<Portfolio>>> getClientPortfolios(@PathVariable String clientId) {
        log.info("Fetching portfolios for client: {}", clientId);

        List<Portfolio> portfolios = portfolioService.findByClientId(clientId);
        return ResponseEntity.ok(ApiResponse.success(portfolios));
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Portfolio>> updatePortfolio(
            @PathVariable String portfolioId,
            @Valid @RequestBody Portfolio portfolioUpdate) {
        try {
            MDC.put("portfolioId", portfolioId);
            log.info("Updating portfolio: {}", portfolioId);

            Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolioId, portfolioUpdate);
            return ResponseEntity.ok(ApiResponse.success(updatedPortfolio));
        } catch (IllegalArgumentException e) {
            log.warn("Portfolio not found for update: {}", portfolioId);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(@PathVariable String portfolioId) {
        try {
            MDC.put("portfolioId", portfolioId);
            log.info("Deleting portfolio: {}", portfolioId);

            portfolioService.deletePortfolio(portfolioId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (IllegalArgumentException e) {
            log.warn("Portfolio not found for deletion: {}", portfolioId);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        } finally {
            MDC.clear();
        }
    }
}