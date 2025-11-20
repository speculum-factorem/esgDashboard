package com.esg.dashboard.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeRangeUtilsTest {

    @Test
    void getStartOfDay_ShouldTruncateToDay() {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);

        // Act
        LocalDateTime result = TimeRangeUtils.getStartOfDay(dateTime);

        // Assert
        assertEquals(LocalDateTime.of(2024, 1, 15, 0, 0, 0), result);
    }

    @Test
    void isWithinLastDays_WhenWithinRange_ShouldReturnTrue() {
        // Arrange
        LocalDateTime recentDateTime = LocalDateTime.now().minusDays(2);

        // Act & Assert
        assertTrue(TimeRangeUtils.isWithinLastDays(recentDateTime, 3));
    }

    @Test
    void formatDuration_ShouldFormatCorrectly() {
        // Arrange
        long milliseconds = 125000; // 2 minutes 5 seconds

        // Act
        String result = TimeRangeUtils.formatDuration(milliseconds);

        // Assert
        assertEquals("2m 5s", result);
    }
}