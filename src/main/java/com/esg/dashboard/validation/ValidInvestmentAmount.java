package com.esg.dashboard.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InvestmentAmountValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInvestmentAmount {
    String message() default "Investment amount must be positive";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}