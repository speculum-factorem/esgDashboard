package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataExportServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private DataExportService dataExportService;

    @Test
    void exportCompaniesToJson_ShouldReturnValidJson() {
        // Arrange
        Company company = createTestCompany();
        when(mongoTemplate.find(new Query(), Company.class)).thenReturn(List.of(company));

        // Act
        String result = dataExportService.exportCompaniesToJson();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("TEST001"));
        assertTrue(result.contains("Test Company"));
    }

    @Test
    void generateExportFilename_ShouldIncludeTimestamp() {
        // Act
        String filename = dataExportService.generateExportFilename("companies");

        // Assert
        assertNotNull(filename);
        assertTrue(filename.startsWith("companies_export_"));
        assertTrue(filename.endsWith(".json"));
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