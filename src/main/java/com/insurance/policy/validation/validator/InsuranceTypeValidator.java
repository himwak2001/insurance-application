package com.insurance.policy.validation.validator;

import com.insurance.policy.validation.annotation.ValidateInsuranceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class InsuranceTypeValidator implements ConstraintValidator<ValidateInsuranceType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        List<String> validTypes = Arrays.asList("HEALTH", "VEHICLE", "LIFE");
        return value != null && validTypes.contains(value.trim().toUpperCase());
    }
}
