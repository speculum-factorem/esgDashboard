package com.esg.dashboard.service;

import com.esg.dashboard.event.EventPublisher;
import com.esg.dashboard.model.Company;
import com.esg.dashboard.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServicePaginationTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private CompanyCacheService companyCacheService;

    @Mock
    private EventPublisher eventPublisher;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, redisTemplate, zSetOperations, 
                companyCacheService, eventPublisher);
    }

    @Test
    void testGetTopRankedCompaniesWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Company> companies = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Company company = new Company();
            company.setCompanyId("COMP" + i);
            companies.add(company);
        }
        Page<Company> page = new org.springframework.data.domain.PageImpl<>(companies, pageable, 50);

        when(companyRepository.findTopRankedCompanies(pageable)).thenReturn(page);

        Page<Company> result = companyService.getTopRankedCompanies(pageable);

        assertNotNull(result);
        assertEquals(10, result.getContent().size());
        assertEquals(50, result.getTotalElements());
        verify(companyRepository).findTopRankedCompanies(pageable);
    }

    @Test
    void testGetCompaniesBySectorWithPagination() {
        String sector = "Technology";
        Pageable pageable = PageRequest.of(0, 20);
        List<Company> companies = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Company company = new Company();
            company.setCompanyId("COMP" + i);
            company.setSector(sector);
            companies.add(company);
        }
        Page<Company> page = new org.springframework.data.domain.PageImpl<>(companies, pageable, 100);

        when(companyRepository.findBySector(sector, pageable)).thenReturn(page);

        Page<Company> result = companyService.getCompaniesBySector(sector, pageable);

        assertNotNull(result);
        assertEquals(20, result.getContent().size());
        assertEquals(100, result.getTotalElements());
        verify(companyRepository).findBySector(sector, pageable);
    }
}

