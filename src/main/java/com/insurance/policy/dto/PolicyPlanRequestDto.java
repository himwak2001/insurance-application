package com.insurance.policy.dto;

import com.insurance.policy.validation.annotation.ValidateInsuranceType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PolicyPlanRequestDto {
    @NotBlank(message = "Plan Name cannot be empty!!!")
    private String planName;

    @ValidateInsuranceType
    private String insuranceType;

    @NotNull(message = "Coverage Amount cannot be null!!!")
    @Positive(message = "Coverage Amount cannot be negative!!!")
    private BigDecimal coverageAmount;

    @NotNull(message = "Base Premium cannot be null!!!")
    @Positive(message = "Base Premium cannot be negative!!!")
    private BigDecimal basePremium;
    private Integer durationMonths;

    @NotNull(message = "Minimum Age cannot be null!!!")
    @Min(18)
    private Integer minAge;

    @NotNull(message = "Maximum Age cannot be null!!!")
    @Max(99)
    private Integer maxAge;
    private String exclusions;
}
