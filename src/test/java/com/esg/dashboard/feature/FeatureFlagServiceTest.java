package com.esg.dashboard.feature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {

    @InjectMocks
    private FeatureFlagService featureFlagService;

    @Test
    void isEnabled_WhenFeatureExists_ShouldReturnValue() {
        // Act & Assert
        assertTrue(featureFlagService.isEnabled("real-time-updates"));
    }

    @Test
    void setFeatureFlag_ShouldUpdateValue() {
        // Arrange
        String feature = "test-feature";

        // Act
        featureFlagService.setFeatureFlag(feature, true);

        // Assert
        assertTrue(featureFlagService.isEnabled(feature));
    }

    @Test
    void getAllFeatureFlags_ShouldReturnAllFlags() {
        // Act
        var flags = featureFlagService.getAllFeatureFlags();

        // Assert
        assertNotNull(flags);
        assertFalse(flags.isEmpty());
    }
}