package com.insurance.policy.validation.annotation;

import com.insurance.policy.validation.validator.InsuranceTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InsuranceTypeValidator.class)
public @interface ValidateInsuranceType {
    String message() default "Invalid Insurance Type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
