package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.feature.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/features")
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> getAllFeatureFlags() {
        log.debug("Fetching all feature flags");

        Map<String, Boolean> flags = featureFlagService.getAllFeatureFlags();
        return ResponseEntity.ok(ApiResponse.success(flags));
    }

    @GetMapping("/{feature}")
    public ResponseEntity<ApiResponse<Boolean>> getFeatureFlag(@PathVariable String feature) {
        log.debug("Checking feature flag: {}", feature);

        boolean enabled = featureFlagService.isEnabled(feature);
        return ResponseEntity.ok(ApiResponse.success(enabled));
    }

    @PutMapping("/{feature}")
    public ResponseEntity<ApiResponse<String>> setFeatureFlag(
            @PathVariable String feature,
            @RequestParam boolean enabled) {

        log.info("Setting feature flag: {} = {}", feature, enabled);

        featureFlagService.setFeatureFlag(feature, enabled);
        return ResponseEntity.ok(ApiResponse.success("Feature flag updated"));
    }
}