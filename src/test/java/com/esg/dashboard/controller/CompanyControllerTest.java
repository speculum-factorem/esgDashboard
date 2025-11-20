package com.esg.dashboard.controller;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.service.CompanyService;
import com.esg.dashboard.service.RealTimeUpdateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompanyService companyService;

    @MockBean
    private RealTimeUpdateService realTimeUpdateService;

    @Test
    void createCompany_ShouldReturnCreatedCompany() throws Exception {
        Company company = createTestCompany();

        when(companyService.saveOrUpdateCompany(any(Company.class))).thenReturn(company);

        mockMvc.perform(post("/api/v1/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(company)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyId").value("TEST001"));
    }

    @Test
    void getCompany_WhenCompanyExists_ShouldReturnCompany() throws Exception {
        Company company = createTestCompany();

        when(companyService.findByCompanyId("TEST001")).thenReturn(Optional.of(company));

        mockMvc.perform(get("/api/v1/companies/TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.companyId").value("TEST001"));
    }

    @Test
    void getCompany_WhenCompanyNotExists_ShouldReturnNotFound() throws Exception {
        when(companyService.findByCompanyId("NONEXISTENT")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/companies/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
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
                .id("1")
                .companyId("TEST001")
                .name("Test Company")
                .sector("Technology")
                .currentRating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}