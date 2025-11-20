package com.esg.dashboard.validation;

import com.esg.dashboard.model.ESGRating;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ESGDataValidator {

    private static final double MIN_SCORE = 0.0;
    private static final double MAX_SCORE = 100.0;
    private static final double MIN_CARBON = 0.0;
    private static final double MAX_CARBON = 10000.0;

    public static boolean isValidRating(ESGRating rating) {
        if (rating == null) {
            log.warn("ESG rating is null");
            return false;
        }

        return isValidScore(rating.getOverallScore()) &&
                isValidScore(rating.getEnvironmentalScore()) &&
                isValidScore(rating.getSocialScore()) &&
                isValidScore(rating.getGovernanceScore()) &&
                isValidCarbonFootprint(rating.getCarbonFootprint()) &&
                isValidScore(rating.getSocialImpactScore());
    }

    private static boolean isValidScore(Double score) {
        return score != null && score >= MIN_SCORE && score <= MAX_SCORE;
    }

    private static boolean isValidCarbonFootprint(Double carbon) {
        return carbon != null && carbon >= MIN_CARBON && carbon <= MAX_CARBON;
    }

    public static void validateRating(ESGRating rating) {
        if (!isValidRating(rating)) {
            throw new IllegalArgumentException("Invalid ESG rating data");
        }
    }
}