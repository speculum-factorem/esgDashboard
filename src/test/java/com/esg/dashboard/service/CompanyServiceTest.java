package com.esg.dashboard.service;

import com.esg.dashboard.event.EventPublisher;
import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.repository.CompanyRepository;
import com.esg.dashboard.service.CompanyCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

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

    private Company testCompany;

    @BeforeEach
    void setUp() {
        companyService = new CompanyService(companyRepository, redisTemplate, zSetOperations, 
                companyCacheService, eventPublisher);

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
    }

    @Test
    void saveOrUpdateCompany_ShouldSaveCompany() {
        // Arrange
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);
        when(redisTemplate.opsForValue()).thenReturn(mock(org.springframework.data.redis.core.ValueOperations.class));

        // Act
        Company result = companyService.saveOrUpdateCompany(testCompany);

        // Assert
        assertNotNull(result);
        assertEquals(testCompany.getCompanyId(), result.getCompanyId());
        verify(companyRepository, times(1)).save(testCompany);
    }

    @Test
    void findByCompanyId_WhenCompanyExists_ShouldReturnCompany() {
        // Arrange
        when(companyRepository.findByCompanyId("COMP001")).thenReturn(Optional.of(testCompany));

        // Act
        Optional<Company> result = companyService.findByCompanyId("COMP001");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("COMP001", result.get().getCompanyId());
    }

    @Test
    void updateESGRating_WhenCompanyExists_ShouldUpdateRating() {
        // Arrange
        ESGRating newRating = ESGRating.builder()
                .overallScore(90.0)
                .environmentalScore(95.0)
                .socialScore(85.0)
                .governanceScore(90.0)
                .carbonFootprint(100.0)
                .socialImpactScore(85.0)
                .ratingGrade("AAA")
                .calculationDate(LocalDateTime.now())
                .build();

        when(companyRepository.findByCompanyId("COMP001")).thenReturn(Optional.of(testCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        // Act & Assert
        assertDoesNotThrow(() -> companyService.updateESGRating("COMP001", newRating));

        verify(companyRepository, times(1)).save(any(Company.class));
    }
}