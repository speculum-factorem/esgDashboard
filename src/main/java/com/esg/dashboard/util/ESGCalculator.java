package com.esg.dashboard.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ESGCalculator {

    public static double calculateWeightedScore(double environmental, double social, double governance,
                                                double envWeight, double socialWeight, double govWeight) {
        return (environmental * envWeight + social * socialWeight + governance * govWeight)
                / (envWeight + socialWeight + govWeight);
    }

    public static String calculateRatingGrade(double score) {
        if (score >= 90) return "AAA";
        if (score >= 80) return "AA";
        if (score >= 70) return "A";
        if (score >= 60) return "BBB";
        if (score >= 50) return "BB";
        if (score >= 40) return "B";
        return "C";
    }

    public static boolean isSignificantChange(double previous, double current, double threshold) {
        return Math.abs(current - previous) >= threshold;
    }
}