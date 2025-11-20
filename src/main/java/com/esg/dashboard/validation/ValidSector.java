package com.esg.dashboard.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SectorValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSector {
    String message() default "Invalid sector. Allowed values: Technology, Energy, Finance, Healthcare, Manufacturing";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}