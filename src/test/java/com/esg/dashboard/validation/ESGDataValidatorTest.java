package com.esg.dashboard.validation;

import com.esg.dashboard.model.ESGRating;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ESGDataValidatorTest {

    @Test
    void isValidRating_WhenValidRating_ShouldReturnTrue() {
        // Arrange
        ESGRating rating = ESGRating.builder()
                .overallScore(85.5)
                .environmentalScore(90.0)
                .socialScore(80.0)
                .governanceScore(86.5)
                .carbonFootprint(120.5)
                .socialImpactScore(78.0)
                .build();

        // Act & Assert
        assertTrue(ESGDataValidator.isValidRating(rating));
    }

    @Test
    void isValidRating_WhenInvalidScore_ShouldReturnFalse() {
        // Arrange
        ESGRating rating = ESGRating.builder()
                .overallScore(150.0) // Invalid score
                .environmentalScore(90.0)
                .socialScore(80.0)
                .governanceScore(86.5)
                .carbonFootprint(120.5)
                .socialImpactScore(78.0)
                .build();

        // Act & Assert
        assertFalse(ESGDataValidator.isValidRating(rating));
    }

    @Test
    void validateRating_WhenValidRating_ShouldNotThrow() {
        // Arrange
        ESGRating rating = ESGRating.builder()
                .overallScore(85.5)
                .environmentalScore(90.0)
                .socialScore(80.0)
                .governanceScore(86.5)
                .carbonFootprint(120.5)
                .socialImpactScore(78.0)
                .build();

        // Act & Assert
        assertDoesNotThrow(() -> ESGDataValidator.validateRating(rating));
    }
}