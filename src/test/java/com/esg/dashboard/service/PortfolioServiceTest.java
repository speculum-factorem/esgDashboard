package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.model.Portfolio;
import com.esg.dashboard.model.PortfolioItem;
import com.esg.dashboard.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private CompanyService companyService;

    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioService(portfolioRepository, companyService);

        ESGRating rating = ESGRating.builder()
                .overallScore(85.5)
                .environmentalScore(90.0)
                .socialScore(80.0)
                .governanceScore(86.5)
                .carbonFootprint(120.5)
                .socialImpactScore(78.0)
                .ratingGrade("AA")
                .calculationDate(LocalDateTime.now())
                .build();

        testCompany = Company.builder()
                .id("1")
                .companyId("COMP001")
                .name("Test Company")
                .sector("Technology")
                .currentRating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        PortfolioItem item = PortfolioItem.builder()
                .companyId("COMP001")
                .investmentAmount(100000.0)
                .build();

        testPortfolio = Portfolio.builder()
                .id("1")
                .portfolioId("PORT001")
                .portfolioName("Test Portfolio")
                .clientId("CLIENT001")
                .clientName("Test Client")
                .items(List.of(item))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPortfolio_ShouldCreatePortfolio() {
        // Arrange
        Map<String, Company> companiesMap = new HashMap<>();
        companiesMap.put("COMP001", testCompany);
        when(companyService.batchLoadCompanies(anyList())).thenReturn(companiesMap);
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

        // Act
        Portfolio result = portfolioService.createPortfolio(testPortfolio);

        // Assert
        assertNotNull(result);
        assertEquals("PORT001", result.getPortfolioId());
        assertNotNull(result.getAggregateScores());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    void createPortfolio_WithZeroInvestment_ShouldHandleGracefully() {
        // Arrange
        PortfolioItem item = PortfolioItem.builder()
                .companyId("COMP001")
                .investmentAmount(0.0)
                .build();
        Portfolio portfolioWithZero = Portfolio.builder()
                .portfolioId("PORT002")
                .portfolioName("Zero Investment Portfolio")
                .items(List.of(item))
                .build();

        Map<String, Company> companiesMap = new HashMap<>();
        companiesMap.put("COMP001", testCompany);
        when(companyService.batchLoadCompanies(anyList())).thenReturn(companiesMap);
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolioWithZero);

        // Act
        Portfolio result = portfolioService.createPortfolio(portfolioWithZero);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAggregateScores());
        assertEquals("N/A", result.getAggregateScores().getAverageRating());
        assertEquals(0.0, result.getAggregateScores().getTotalInvestment());
    }

    @Test
    void createPortfolio_WithEmptyItems_ShouldHandleGracefully() {
        // Arrange
        Portfolio emptyPortfolio = Portfolio.builder()
                .portfolioId("PORT003")
                .portfolioName("Empty Portfolio")
                .items(List.of())
                .build();

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(emptyPortfolio);

        // Act
        Portfolio result = portfolioService.createPortfolio(emptyPortfolio);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAggregateScores());
        assertEquals("N/A", result.getAggregateScores().getAverageRating());
        assertEquals(0, result.getAggregateScores().getTotalCompanies());
    }

    @Test
    void findByPortfolioId_WhenPortfolioExists_ShouldReturnPortfolio() {
        // Arrange
        when(portfolioRepository.findByPortfolioId("PORT001")).thenReturn(Optional.of(testPortfolio));

        // Act
        Optional<Portfolio> result = portfolioService.findByPortfolioId("PORT001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("PORT001", result.get().getPortfolioId());
    }
}