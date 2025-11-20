package com.esg.dashboard.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class SectorValidator implements ConstraintValidator<ValidSector, String> {

    private static final Set<String> VALID_SECTORS = Set.of(
            "Technology", "Energy", "Finance", "Healthcare", "Manufacturing",
            "Real Estate", "Consumer Goods", "Utilities", "Telecommunications"
    );

    @Override
    public boolean isValid(String sector, ConstraintValidatorContext context) {
        if (sector == null) {
            return false;
        }
        return VALID_SECTORS.contains(sector);
    }
}