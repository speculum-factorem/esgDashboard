package com.esg.dashboard.integration;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.repository.CompanyRepository;
import com.esg.dashboard.service.CompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class CompanyServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void saveAndRetrieveCompany_ShouldWorkCorrectly() {
        // Arrange
        Company company = createTestCompany();

        // Act
        Company savedCompany = companyService.saveOrUpdateCompany(company);
        Optional<Company> retrievedCompany = companyService.findByCompanyId("TEST001");

        // Assert
        assertTrue(retrievedCompany.isPresent());
        assertEquals("TEST001", retrievedCompany.get().getCompanyId());
        assertEquals("Test Company", retrievedCompany.get().getName());
    }

    @Test
    void updateESGRating_ShouldUpdateAndCache() {
        // Arrange
        Company company = createTestCompany();
        Company savedCompany = companyService.saveOrUpdateCompany(company);

        ESGRating newRating = ESGRating.builder()
                .overallScore(95.0)
                .environmentalScore(98.0)
                .socialScore(92.0)
                .governanceScore(95.0)
                .carbonFootprint(80.0)
                .socialImpactScore(90.0)
                .ratingGrade("AAA")
                .calculationDate(LocalDateTime.now())
                .build();

        // Act
        companyService.updateESGRating("TEST001", newRating);
        Optional<Company> updatedCompany = companyService.findByCompanyId("TEST001");

        // Assert
        assertTrue(updatedCompany.isPresent());
        assertEquals(95.0, updatedCompany.get().getCurrentRating().getOverallScore());
        assertEquals("AAA", updatedCompany.get().getCurrentRating().getRatingGrade());
    }

    private Company createTestCompany() {
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

        return Company.builder()
                .companyId("TEST001")
                .name("Test Company")
                .sector("Technology")
                .currentRating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}