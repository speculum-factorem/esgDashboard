package com.esg.dashboard.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

/**
 * Сервис для получения ESG данных из внешних API
 * Интегрируется с внешними источниками данных для синхронизации ESG показателей
 */
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
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "FETCH_EXTERNAL_ESG_DATA");
            
            if (!isExternalApiConfigured()) {
                log.warn("External ESG API is not configured");
                return Map.of("error", "External API not configured");
            }

            String url = String.format("%s/companies/%s/esg", esgApiUrl, companyId);
            log.info("Fetching ESG data from external API for company: {}", companyId);

            // Добавляем API ключ в заголовки
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            RequestEntity<Void> request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    request, 
                    org.springframework.core.ParameterizedTypeReference.forType(Map.class)
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Successfully fetched ESG data from external API for company: {}", companyId);
                return response.getBody();
            } else {
                log.warn("Failed to fetch ESG data for company: {}, Status: {}",
                        companyId, response.getStatusCode());
                return Map.of("error", "Failed to fetch data");
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("HTTP error fetching ESG data for company {}: {} - {}",
                    companyId, e.getStatusCode(), e.getMessage());
            return Map.of("error", "HTTP error: " + e.getStatusCode());
        } catch (org.springframework.web.client.ResourceAccessException e) {
            log.error("Connection error to external API for company {}: {}", companyId, e.getMessage());
            return Map.of("error", "Connection error to external API");
        } catch (Exception e) {
            log.error("Error fetching ESG data for company {}: {}", companyId, e.getMessage(), e);
            return Map.of("error", e.getMessage());
        } finally {
            MDC.clear();
        }
    }

    public boolean isExternalApiConfigured() {
        return esgApiUrl != null && !esgApiUrl.isEmpty() &&
                apiKey != null && !apiKey.isEmpty();
    }

    public void syncCompanyData(String companyId) {
        try {
            MDC.put("companyId", companyId);
            MDC.put("operation", "SYNC_COMPANY_DATA");
            
            if (!isExternalApiConfigured()) {
                log.debug("External API is not configured, skipping sync for company: {}", companyId);
                return;
            }

            log.info("Starting company data sync from external API: {}", companyId);
            Map<String, Object> externalData = fetchCompanyESGData(companyId);

            if (!externalData.containsKey("error")) {
                log.info("External ESG data successfully synced for company: {}", companyId);
                // Здесь можно добавить логику обновления данных в базе
                // Например: companyService.updateFromExternalData(companyId, externalData);
            } else {
                log.warn("Error syncing data for company {}: {}", 
                        companyId, externalData.get("error"));
            }

        } catch (Exception e) {
            log.error("Failed to sync company data for {}: {}", companyId, e.getMessage(), e);
        } finally {
            MDC.clear();
        }
    }
}