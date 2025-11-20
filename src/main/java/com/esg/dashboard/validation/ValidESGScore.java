package com.esg.dashboard.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ESGScoreValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidESGScore {
    String message() default "ESG score must be between 0 and 100";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}