package com.esg.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class ExternalESGDataService {

    private final RestTemplate restTemplate;

    @Value("${app.external.esg.api.url:}")
    private String esgApiUrl;

    @Value("${app.external.esg.api.key:}")
    private String apiKey;

    public ExternalESGDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> fetchCompanyESGData(String companyId) {
        if (!isExternalApiConfigured()) {
            log.warn("External ESG API is not configured");
            return Map.of("error", "External API not configured");
        }

        try {
            String url = String.format("%s/companies/%s/esg", esgApiUrl, companyId);

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched ESG data for company: {}", companyId);
                return response.getBody();
            } else {
                log.warn("Failed to fetch ESG data for company: {}, Status: {}",
                        companyId, response.getStatusCode());
                return Map.of("error", "Failed to fetch data");
            }

        } catch (Exception e) {
            log.error("Error fetching ESG data for company {}: {}", companyId, e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    public boolean isExternalApiConfigured() {
        return esgApiUrl != null && !esgApiUrl.isEmpty() &&
                apiKey != null && !apiKey.isEmpty();
    }

    public void syncCompanyData(String companyId) {
        if (!isExternalApiConfigured()) {
            return;
        }

        try {
            Map<String, Object> externalData = fetchCompanyESGData(companyId);

            if (!externalData.containsKey("error")) {
                log.info("Successfully synced external ESG data for company: {}", companyId);
                // Здесь можно добавить логику обновления данных в базе
            }

        } catch (Exception e) {
            log.error("Failed to sync company data for {}: {}", companyId, e.getMessage());
        }
    }
}