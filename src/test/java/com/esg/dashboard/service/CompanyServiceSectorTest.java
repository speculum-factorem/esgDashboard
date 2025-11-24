package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceSectorTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, redisTemplate, zSetOperations);
    }

    @Test
    void getCompaniesBySector_WhenSectorExists_ShouldReturnCompanies() {
        // Arrange
        String sector = "Technology";
        Company company1 = Company.builder()
                .companyId("COMP001")
                .name("Tech Corp 1")
                .sector(sector)
                .build();
        Company company2 = Company.builder()
                .companyId("COMP002")
                .name("Tech Corp 2")
                .sector(sector)
                .build();

        when(companyRepository.findBySector(sector)).thenReturn(Arrays.asList(company1, company2));

        // Act
        List<Company> result = companyService.getCompaniesBySector(sector);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(c -> sector.equals(c.getSector())));
        verify(companyRepository, times(1)).findBySector(sector);
    }

    @Test
    void getCompaniesBySector_WhenSectorEmpty_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            companyService.getCompaniesBySector(""));
    }

    @Test
    void getCompaniesBySector_WhenSectorNull_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            companyService.getCompaniesBySector(null));
    }
}

