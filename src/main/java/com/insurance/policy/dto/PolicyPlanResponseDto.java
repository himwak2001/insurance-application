package com.insurance.policy.dto;

import com.insurance.policy.entity.InsuranceType;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PolicyPlanResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String planName;
    private InsuranceType insuranceType;
    private BigDecimal coverageAmount;
    private BigDecimal basePremium;
    private Integer durationMonths;
    private Integer minAge;
    private Integer maxAge;
    private String exclusions;
}
