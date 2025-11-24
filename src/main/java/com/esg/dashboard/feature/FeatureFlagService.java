package com.esg.dashboard.feature;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для управления feature flags (функциональными флагами)
 * Позволяет включать/выключать функции без перезапуска приложения
 */
@Slf4j
@Service
public class FeatureFlagService {

    private final Map<String, Boolean> featureFlags = new ConcurrentHashMap<>();

    @Value("${app.features.real-time-updates:true}")
    private boolean realTimeUpdatesEnabled;

    @Value("${app.features.historical-data:true}")
    private boolean historicalDataEnabled;

    @Value("${app.features.email-notifications:false}")
    private boolean emailNotificationsEnabled;

    @Value("${app.features.external-api-sync:false}")
    private boolean externalApiSyncEnabled;

    public FeatureFlagService() {
        initializeFeatureFlags();
    }

    private void initializeFeatureFlags() {
        try {
            MDC.put("operation", "INITIALIZE_FEATURE_FLAGS");
            featureFlags.put("real-time-updates", realTimeUpdatesEnabled);
            featureFlags.put("historical-data", historicalDataEnabled);
            featureFlags.put("email-notifications", emailNotificationsEnabled);
            featureFlags.put("external-api-sync", externalApiSyncEnabled);

            log.info("Feature flags initialized: {}", featureFlags);
        } finally {
            MDC.clear();
        }
    }

    public boolean isEnabled(String feature) {
        try {
            MDC.put("operation", "CHECK_FEATURE_FLAG");
            MDC.put("feature", feature);
            boolean enabled = featureFlags.getOrDefault(feature, false);
            log.debug("Checking feature flag: {} = {}", feature, enabled);
            return enabled;
        } finally {
            MDC.clear();
        }
    }

    public void setFeatureFlag(String feature, boolean enabled) {
        try {
            MDC.put("operation", "SET_FEATURE_FLAG");
            MDC.put("feature", feature);
            featureFlags.put(feature, enabled);
            log.info("Feature flag updated: {} = {}", feature, enabled);
        } finally {
            MDC.clear();
        }
    }

    public Map<String, Boolean> getAllFeatureFlags() {
        try {
            MDC.put("operation", "GET_ALL_FEATURE_FLAGS");
            log.debug("Fetching all feature flags");
            return new ConcurrentHashMap<>(featureFlags);
        } finally {
            MDC.clear();
        }
    }

    public boolean areRealTimeUpdatesEnabled() {
        return isEnabled("real-time-updates");
    }

    public boolean isHistoricalDataEnabled() {
        return isEnabled("historical-data");
    }

    public boolean areEmailNotificationsEnabled() {
        return isEnabled("email-notifications");
    }

    public boolean isExternalApiSyncEnabled() {
        return isEnabled("external-api-sync");
    }
}