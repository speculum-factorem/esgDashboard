package com.esg.dashboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InvestmentAmountValidator implements ConstraintValidator<ValidInvestmentAmount, Double> {

    @Override
    public boolean isValid(Double amount, ConstraintValidatorContext context) {
        if (amount == null) {
            return false;
        }
        return amount > 0;
    }
}