package com.insurance.policy.helper;

import com.insurance.policy.dto.PolicyPlanRequestDto;
import com.insurance.policy.dto.PolicyPlanResponseDto;
import com.insurance.policy.entity.InsuranceType;
import com.insurance.policy.entity.PolicyPlan;

public class PolicyPlanMapper {
    public static void mapPolicyPlanRequestDtoToPolicyPlan(PolicyPlanRequestDto requestDto, PolicyPlan policyPlan, boolean isUpdate) {
        policyPlan.setPlanName(requestDto.getPlanName());
        if (!isUpdate)
            policyPlan.setInsuranceType(InsuranceType.valueOf(requestDto.getInsuranceType().trim().toUpperCase()));
        policyPlan.setCoverageAmount(requestDto.getCoverageAmount());
        policyPlan.setBasePremium(requestDto.getBasePremium());
        policyPlan.setDurationMonths(requestDto.getDurationMonths());
        policyPlan.setMinAge(requestDto.getMinAge());
        policyPlan.setMaxAge(requestDto.getMaxAge());
        policyPlan.setExclusions(requestDto.getExclusions());
    }

    public static void mapPolicyPlanToPolicyPlanResponseDto(PolicyPlanResponseDto responseDto, PolicyPlan policyPlan) {
        responseDto.setId(policyPlan.getId());
        responseDto.setPlanName(policyPlan.getPlanName());
        responseDto.setInsuranceType(policyPlan.getInsuranceType());
        responseDto.setCoverageAmount(policyPlan.getCoverageAmount());
        responseDto.setBasePremium(policyPlan.getBasePremium());
        responseDto.setDurationMonths(policyPlan.getDurationMonths());
        responseDto.setMinAge(policyPlan.getMinAge());
        responseDto.setMaxAge(policyPlan.getMaxAge());
        responseDto.setExclusions(policyPlan.getExclusions());
    }
}
