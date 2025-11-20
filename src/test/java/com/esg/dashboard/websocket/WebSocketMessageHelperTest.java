package com.esg.dashboard.websocket;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WebSocketMessageHelperTest {

    @InjectMocks
    private WebSocketMessageHelper webSocketMessageHelper;

    @Test
    void createCompanyUpdateMessage_ShouldCreateValidJson() {
        // Arrange
        Company company = createTestCompany();

        // Act
        String message = webSocketMessageHelper.createCompanyUpdateMessage(company);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("COMPANY_UPDATE"));
        assertTrue(message.contains("TEST001"));
        assertTrue(message.contains("Test Company"));
    }

    @Test
    void createErrorMessage_ShouldCreateErrorJson() {
        // Arrange
        String error = "Test error";
        String details = "Test details";

        // Act
        String message = webSocketMessageHelper.createErrorMessage(error, details);

        // Assert
        assertNotNull(message);
        assertTrue(message.contains("ERROR"));
        assertTrue(message.contains("Test error"));
        assertTrue(message.contains("Test details"));
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