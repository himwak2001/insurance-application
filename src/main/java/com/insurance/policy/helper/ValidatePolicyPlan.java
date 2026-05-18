package com.insurance.policy.helper;

import com.insurance.policy.dto.PolicyPlanRequestDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidatePolicyPlan {
    public List<String> validatePolicyPlan(PolicyPlanRequestDto requestDto) {
        List<String> validationErrors = new ArrayList<>();
        if (requestDto.getBasePremium().compareTo(requestDto.getCoverageAmount()) >= 0) {
            validationErrors.add("Coverage Amount must be less than Base Premium!");
        }
        if (requestDto.getMinAge() >= requestDto.getMaxAge()) {
            validationErrors.add("Minimum Age must be less than Maximum Age!");
        }

        BigDecimal remainder = requestDto.getCoverageAmount().remainder(new BigDecimal("50000"));
        if (remainder.compareTo(BigDecimal.ZERO) != 0) {
            validationErrors.add("Coverage Amount must be divisible of 50,000");
        }
        return validationErrors;
    }
}
