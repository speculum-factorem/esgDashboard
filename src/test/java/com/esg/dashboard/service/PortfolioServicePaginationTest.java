package com.esg.dashboard.service;

import com.esg.dashboard.model.Portfolio;
import com.esg.dashboard.repository.PortfolioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServicePaginationTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private CompanyService companyService;

    private PortfolioService portfolioService;

    @BeforeEach
    void setUp() {
        portfolioService = new PortfolioService(portfolioRepository, companyService);
    }

    @Test
    void testFindByClientIdWithPagination() {
        String clientId = "CLIENT001";
        Pageable pageable = PageRequest.of(0, 10);
        List<Portfolio> portfolios = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Portfolio portfolio = new Portfolio();
            portfolio.setPortfolioId("PORT" + i);
            portfolio.setClientId(clientId);
            portfolios.add(portfolio);
        }
        Page<Portfolio> page = new org.springframework.data.domain.PageImpl<>(portfolios, pageable, 50);

        when(portfolioRepository.findByClientId(clientId, pageable)).thenReturn(page);

        Page<Portfolio> result = portfolioService.findByClientId(clientId, pageable);

        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(50, result.getTotalElements());
        verify(portfolioRepository).findByClientId(clientId, pageable);
    }
}

