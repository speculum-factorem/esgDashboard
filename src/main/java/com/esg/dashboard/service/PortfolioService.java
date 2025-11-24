package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.Portfolio;
import com.esg.dashboard.model.PortfolioAggregate;
import com.esg.dashboard.model.PortfolioItem;
import com.esg.dashboard.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final CompanyService companyService;

    public Portfolio createPortfolio(Portfolio portfolio) {
        Assert.notNull(portfolio, "Portfolio cannot be null");

        try {
            MDC.put("portfolioId", portfolio.getPortfolioId());
            MDC.put("clientId", portfolio.getClientId());
            log.info("Creating new portfolio: {}", portfolio.getPortfolioName());

            // Calculate aggregate scores
            Portfolio portfolioWithScores = calculateAggregateScores(portfolio);
            portfolioWithScores.setCreatedAt(LocalDateTime.now());
            portfolioWithScores.setUpdatedAt(LocalDateTime.now());

            Portfolio savedPortfolio = portfolioRepository.save(portfolioWithScores);

            log.info("Portfolio created successfully with ID: {}", savedPortfolio.getId());
            return savedPortfolio;
        } finally {
            MDC.clear();
        }
    }

    public Optional<Portfolio> findByPortfolioId(String portfolioId) {
        Assert.hasText(portfolioId, "Portfolio ID cannot be empty");

        try {
            MDC.put("portfolioId", portfolioId);
            log.debug("Fetching portfolio: {}", portfolioId);

            return portfolioRepository.findByPortfolioId(portfolioId);
        } finally {
            MDC.clear();
        }
    }

    public List<Portfolio> findByClientId(String clientId) {
        Assert.hasText(clientId, "Client ID cannot be empty");

        log.info("Fetching portfolios for client: {}", clientId);
        return portfolioRepository.findByClientId(clientId);
    }

    public org.springframework.data.domain.Page<Portfolio> findByClientId(String clientId, org.springframework.data.domain.Pageable pageable) {
        Assert.hasText(clientId, "Client ID cannot be empty");

        try {
            MDC.put("clientId", clientId);
            MDC.put("page", String.valueOf(pageable.getPageNumber()));
            MDC.put("size", String.valueOf(pageable.getPageSize()));
            log.info("Fetching portfolios for client: {} - page: {}, size: {}", clientId, pageable.getPageNumber(), pageable.getPageSize());

            org.springframework.data.domain.Page<Portfolio> portfolios = portfolioRepository.findByClientId(clientId, pageable);
            log.debug("Found {} portfolios for client {} (page {})", portfolios.getContent().size(), clientId, pageable.getPageNumber());
            return portfolios;
        } finally {
            MDC.clear();
        }
    }

    public Portfolio updatePortfolio(String portfolioId, Portfolio portfolioUpdate) {
        Assert.hasText(portfolioId, "Portfolio ID cannot be empty");
        Assert.notNull(portfolioUpdate, "Portfolio update cannot be null");

        try {
            MDC.put("portfolioId", portfolioId);
            log.info("Updating portfolio: {}", portfolioId);

            Portfolio existingPortfolio = portfolioRepository.findByPortfolioId(portfolioId)
                    .orElseThrow(() -> {
                        log.error("Portfolio not found: {}", portfolioId);
                        return new IllegalArgumentException("Portfolio not found: " + portfolioId);
                    });

            // Update fields
            existingPortfolio.setPortfolioName(portfolioUpdate.getPortfolioName());
            existingPortfolio.setItems(portfolioUpdate.getItems());
            existingPortfolio.setUpdatedAt(LocalDateTime.now());

            // Recalculate scores
            Portfolio updatedPortfolio = calculateAggregateScores(existingPortfolio);
            Portfolio savedPortfolio = portfolioRepository.save(updatedPortfolio);

            log.info("Portfolio updated successfully: {}", portfolioId);
            return savedPortfolio;
        } finally {
            MDC.clear();
        }
    }

    public void deletePortfolio(String portfolioId) {
        Assert.hasText(portfolioId, "Portfolio ID cannot be empty");

        try {
            MDC.put("portfolioId", portfolioId);
            log.info("Deleting portfolio: {}", portfolioId);

            Portfolio portfolio = portfolioRepository.findByPortfolioId(portfolioId)
                    .orElseThrow(() -> {
                        log.error("Portfolio not found for deletion: {}", portfolioId);
                        return new IllegalArgumentException("Portfolio not found: " + portfolioId);
                    });

            portfolioRepository.delete(portfolio);
            log.info("Portfolio deleted successfully: {}", portfolioId);
        } finally {
            MDC.clear();
        }
    }

    private Portfolio calculateAggregateScores(Portfolio portfolio) {
        List<PortfolioItem> enrichedItems = new ArrayList<>();
        double totalInvestment = 0.0;
        double weightedEsgScore = 0.0;
        double weightedCarbonFootprint = 0.0;
        double weightedSocialImpact = 0.0;

        if (portfolio.getItems() == null || portfolio.getItems().isEmpty()) {
            log.warn("Portfolio has no items, returning empty aggregate");
            portfolio.setAggregateScores(PortfolioAggregate.builder()
                    .totalEsgScore(0.0)
                    .carbonFootprint(0.0)
                    .socialImpactScore(0.0)
                    .averageRating("N/A")
                    .totalCompanies(0)
                    .totalInvestment(0.0)
                    .build());
            return portfolio;
        }

        // Сначала вычисляем общий объем инвестиций
        for (PortfolioItem item : portfolio.getItems()) {
            if (item.getInvestmentAmount() != null && item.getInvestmentAmount() > 0) {
                totalInvestment += item.getInvestmentAmount();
            }
        }

        if (totalInvestment == 0.0) {
            log.warn("Portfolio total investment is zero, cannot calculate weighted scores");
            portfolio.setAggregateScores(PortfolioAggregate.builder()
                    .totalEsgScore(0.0)
                    .carbonFootprint(0.0)
                    .socialImpactScore(0.0)
                    .averageRating("N/A")
                    .totalCompanies(0)
                    .totalInvestment(0.0)
                    .build());
            return portfolio;
        }

        // Пакетная загрузка компаний для избежания проблемы N+1
        List<String> companyIds = portfolio.getItems().stream()
                .map(PortfolioItem::getCompanyId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();

        Map<String, Company> companiesMap = companyService.batchLoadCompanies(companyIds);

        // Обогащаем элементы данными компаний и вычисляем весовые показатели
        for (PortfolioItem item : portfolio.getItems()) {
            Company company = companiesMap.get(item.getCompanyId());

            if (company != null && item.getInvestmentAmount() != null && item.getInvestmentAmount() > 0) {
                double weight = item.getInvestmentAmount() / totalInvestment;

                PortfolioItem enrichedItem = PortfolioItem.builder()
                        .companyId(item.getCompanyId())
                        .companyName(company.getName())
                        .investmentAmount(item.getInvestmentAmount())
                        .weight(weight)
                        .currentRating(company.getCurrentRating())
                        .build();

                enrichedItems.add(enrichedItem);

                // Вычисляем весовые показатели
                if (company.getCurrentRating() != null) {
                    if (company.getCurrentRating().getOverallScore() != null) {
                        weightedEsgScore += company.getCurrentRating().getOverallScore() * weight;
                    }
                    if (company.getCurrentRating().getCarbonFootprint() != null) {
                        weightedCarbonFootprint += company.getCurrentRating().getCarbonFootprint() * weight;
                    }
                    if (company.getCurrentRating().getSocialImpactScore() != null) {
                        weightedSocialImpact += company.getCurrentRating().getSocialImpactScore() * weight;
                    }
                }
            } else {
                log.warn("Company not found or invalid investment amount for companyId: {}", item.getCompanyId());
            }
        }

        // Устанавливаем обогащенные элементы
        portfolio.setItems(enrichedItems);

        // Вычисляем агрегированные показатели
        PortfolioAggregate aggregate = PortfolioAggregate.builder()
                .totalEsgScore(round(weightedEsgScore, 2))
                .carbonFootprint(round(weightedCarbonFootprint, 2))
                .socialImpactScore(round(weightedSocialImpact, 2))
                .averageRating(calculateRatingGrade(weightedEsgScore))
                .totalCompanies(enrichedItems.size())
                .totalInvestment(round(totalInvestment, 2))
                .build();

        portfolio.setAggregateScores(aggregate);

        log.debug("Calculated aggregate scores for portfolio: ESG={}, Carbon={}, Social={}",
                weightedEsgScore, weightedCarbonFootprint, weightedSocialImpact);

        return portfolio;
    }

    private String calculateRatingGrade(double score) {
        if (score >= 90) return "AAA";
        if (score >= 80) return "AA";
        if (score >= 70) return "A";
        if (score >= 60) return "BBB";
        if (score >= 50) return "BB";
        if (score >= 40) return "B";
        return "C";
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}