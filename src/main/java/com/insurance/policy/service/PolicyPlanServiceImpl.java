package com.insurance.policy.service;

import com.insurance.common.exception.PolicyValidationException;
import com.insurance.common.exception.ResourceAlreadyExistException;
import com.insurance.common.exception.ResourceNotFoundException;
import com.insurance.policy.dto.PolicyPlanRequestDto;
import com.insurance.policy.dto.PolicyPlanResponseDto;
import com.insurance.policy.entity.InsuranceType;
import com.insurance.policy.entity.PolicyPlan;
import com.insurance.policy.helper.PolicyPlanMapper;
import com.insurance.policy.helper.ValidatePolicyPlan;
import com.insurance.policy.repository.IPolicyPlanRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PolicyPlanServiceImpl implements IPolicyPlanService {
    private final IPolicyPlanRepository policyPlanRepository;

    @CacheEvict(value = "policyPlanCache", key = "#requestDto.planName")
    @Override
    public void createPolicyPlan(PolicyPlanRequestDto requestDto) {
        Optional<PolicyPlan> existingPlan = policyPlanRepository.findByPlanName(requestDto.getPlanName());
        if (existingPlan.isPresent()) {
            throw new ResourceAlreadyExistException("Policy Plan", "Plan Name", requestDto.getPlanName());
        }

        List<String> isValid = ValidatePolicyPlan.validatePolicyPlan(requestDto);
        if (!isValid.isEmpty()) {
            throw new PolicyValidationException(isValid);
        }
        PolicyPlan newPolicyPlan = new PolicyPlan();
        PolicyPlanMapper.mapPolicyPlanRequestDtoToPolicyPlan(requestDto, newPolicyPlan, false);
        newPolicyPlan.setId(UUID.randomUUID());
        newPolicyPlan.setIsActive(true);
        policyPlanRepository.save(newPolicyPlan);
    }

    @CacheEvict(value = "policyPlanCache", key = "#policyId")
    @Override
    public String updatePolicyPlan(PolicyPlanRequestDto requestDto, String policyId) {
        Optional<PolicyPlan> existingPolicy = policyPlanRepository.findById(UUID.fromString(policyId));
        if (existingPolicy.isEmpty()) {
            throw new ResourceNotFoundException("PolicyPlan", "Policy Plan Id", policyId);
        }
        PolicyPlanMapper.mapPolicyPlanRequestDtoToPolicyPlan(requestDto, existingPolicy.get(), true);
        policyPlanRepository.save(existingPolicy.get());
        return policyId;
    }

    @CacheEvict(value = "policyPlanCache", key = "#policyId")
    @Override
    public void deactivatePolicyPlan(String policyId) {
        Optional<PolicyPlan> existingPolicy = policyPlanRepository.findById(UUID.fromString(policyId));
        if (existingPolicy.isEmpty()) {
            throw new ResourceNotFoundException("PolicyPlan", "Policy Plan Id", policyId);
        }
        existingPolicy.get().setIsActive(false);
        policyPlanRepository.save(existingPolicy.get());
    }

    @Cacheable(value = "policyPlanCache", key = "#insuranceType")
    @Override
    public List<PolicyPlanResponseDto> activePolicyPlans(int pageNumber, int pageSize, String insuranceType) {
        return policyPlanRepository.findAllActivePlans(PageRequest.of(pageNumber, pageSize), InsuranceType.valueOf(insuranceType.trim().toUpperCase()));
    }

    @Cacheable(value = "policyPlanCache", key = "#planId")
    @Override
    public PolicyPlanResponseDto getPolicyPlan(String planId) {
        Optional<PolicyPlan> existingPolicy = policyPlanRepository.findById(UUID.fromString(planId));
        if (existingPolicy.isEmpty()) {
            throw new ResourceNotFoundException("PolicyPlan", "Policy Plan Id", planId);
        }
        PolicyPlanResponseDto responseDto = new PolicyPlanResponseDto();
        PolicyPlanMapper.mapPolicyPlanToPolicyPlanResponseDto(responseDto, existingPolicy.get());
        return responseDto;
    }


}
