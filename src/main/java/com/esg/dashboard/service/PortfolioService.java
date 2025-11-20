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

        // Calculate total investment first
        for (PortfolioItem item : portfolio.getItems()) {
            totalInvestment += item.getInvestmentAmount();
        }

        // Enrich items with company data and calculate weighted scores
        for (PortfolioItem item : portfolio.getItems()) {
            Optional<Company> companyOpt = companyService.findByCompanyId(item.getCompanyId());

            if (companyOpt.isPresent()) {
                Company company = companyOpt.get();
                double weight = item.getInvestmentAmount() / totalInvestment;

                PortfolioItem enrichedItem = PortfolioItem.builder()
                        .companyId(item.getCompanyId())
                        .companyName(company.getName())
                        .investmentAmount(item.getInvestmentAmount())
                        .weight(weight)
                        .currentRating(company.getCurrentRating())
                        .build();

                enrichedItems.add(enrichedItem);

                // Calculate weighted scores
                if (company.getCurrentRating() != null) {
                    weightedEsgScore += company.getCurrentRating().getOverallScore() * weight;
                    weightedCarbonFootprint += company.getCurrentRating().getCarbonFootprint() * weight;
                    weightedSocialImpact += company.getCurrentRating().getSocialImpactScore() * weight;
                }
            }
        }

        // Set enriched items
        portfolio.setItems(enrichedItems);

        // Calculate aggregate scores
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