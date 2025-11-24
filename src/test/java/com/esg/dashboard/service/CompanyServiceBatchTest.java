package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceBatchTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        companyService = new CompanyService(companyRepository, redisTemplate, zSetOperations);
    }

    @Test
    void batchLoadCompanies_WhenAllInCache_ShouldReturnFromCache() {
        // Arrange
        List<String> companyIds = Arrays.asList("COMP001", "COMP002");
        Company company1 = Company.builder().companyId("COMP001").name("Company 1").build();
        Company company2 = Company.builder().companyId("COMP002").name("Company 2").build();

        when(valueOperations.get("company:COMP001")).thenReturn(company1);
        when(valueOperations.get("company:COMP002")).thenReturn(company2);

        // Act
        Map<String, Company> result = companyService.batchLoadCompanies(companyIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsKey("COMP001"));
        assertTrue(result.containsKey("COMP002"));
        verify(companyRepository, never()).findByCompanyIdIn(anyList());
    }

    @Test
    void batchLoadCompanies_WhenNoneInCache_ShouldLoadFromDatabase() {
        // Arrange
        List<String> companyIds = Arrays.asList("COMP001", "COMP002");
        Company company1 = Company.builder().companyId("COMP001").name("Company 1").build();
        Company company2 = Company.builder().companyId("COMP002").name("Company 2").build();

        when(valueOperations.get(anyString())).thenReturn(null);
        when(companyRepository.findByCompanyIdIn(companyIds)).thenReturn(Arrays.asList(company1, company2));

        // Act
        Map<String, Company> result = companyService.batchLoadCompanies(companyIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository, times(1)).findByCompanyIdIn(companyIds);
    }

    @Test
    void batchLoadCompanies_WhenEmptyList_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            companyService.batchLoadCompanies(List.of()));
    }
}

