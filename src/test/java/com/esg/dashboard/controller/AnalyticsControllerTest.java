package com.esg.dashboard.controller;

import com.esg.dashboard.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void getSectorAnalytics_ShouldReturnAnalytics() throws Exception {
        // Arrange
        Map<String, Object> mockAnalytics = Map.of(
                "sectorBreakdown", Map.of("Technology", 85.5),
                "totalSectors", 5
        );
        when(analyticsService.getSectorAnalytics()).thenReturn(mockAnalytics);

        // Act & Assert
        mockMvc.perform(get("/api/v1/analytics/sectors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSectors").value(5));
    }
}