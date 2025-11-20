package com.esg.dashboard.feature;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        featureFlags.put("real-time-updates", realTimeUpdatesEnabled);
        featureFlags.put("historical-data", historicalDataEnabled);
        featureFlags.put("email-notifications", emailNotificationsEnabled);
        featureFlags.put("external-api-sync", externalApiSyncEnabled);

        log.info("Feature flags initialized: {}", featureFlags);
    }

    public boolean isEnabled(String feature) {
        return featureFlags.getOrDefault(feature, false);
    }

    public void setFeatureFlag(String feature, boolean enabled) {
        featureFlags.put(feature, enabled);
        log.info("Feature flag updated: {} = {}", feature, enabled);
    }

    public Map<String, Boolean> getAllFeatureFlags() {
        return new ConcurrentHashMap<>(featureFlags);
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