package com.esg.dashboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ESGScoreValidator implements ConstraintValidator<ValidESGScore, Double> {

    private static final double MIN_SCORE = 0.0;
    private static final double MAX_SCORE = 100.0;

    @Override
    public boolean isValid(Double score, ConstraintValidatorContext context) {
        if (score == null) {
            return false;
        }
        return score >= MIN_SCORE && score <= MAX_SCORE;
    }
}