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
import java.util.List;
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
        when(companyService.findByCompanyId("COMP001")).thenReturn(Optional.of(testCompany));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

        // Act
        Portfolio result = portfolioService.createPortfolio(testPortfolio);

        // Assert
        assertNotNull(result);
        assertEquals("PORT001", result.getPortfolioId());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
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