package com.insurance.policy.service;

import com.insurance.policy.dto.PolicyPlanRequestDto;
import com.insurance.policy.dto.PolicyPlanResponseDto;

import java.util.List;

public interface IPolicyPlanService {
    // method to create policy plan
    void createPolicyPlan(PolicyPlanRequestDto requestDto);

    // method to update policy
    String updatePolicyPlan(PolicyPlanRequestDto requestDto, String policyId);

    // method to deactivate policy
    void deactivatePolicyPlan(String policyId);

    // method to return all active policy plan with pagination
    List<PolicyPlanResponseDto> activePolicyPlans(int pageNumber, int pageSize, String insuranceType);

    // method to return policy plan based on policy plan id
    PolicyPlanResponseDto getPolicyPlan(String planId);
}
