package com.insurance.policy.controller;

import com.insurance.common.response.ApiResponse;
import com.insurance.policy.dto.PolicyPlanRequestDto;
import com.insurance.policy.dto.PolicyPlanResponseDto;
import com.insurance.policy.service.PolicyPlanServiceImpl;
import com.insurance.policy.validation.annotation.ValidateInsuranceType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/policy-plans")
@RequiredArgsConstructor
@Validated
public class PolicyPlanController {
    private final PolicyPlanServiceImpl serviceImpl;

    @PostMapping
    public ResponseEntity<ApiResponse> createPolicyPlan(@RequestBody @Valid PolicyPlanRequestDto requestDto) {
        serviceImpl.createPolicyPlan(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse(HttpStatus.CREATED.toString(), "Policy Plan created successfully !!!", LocalDateTime.now()));
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ApiResponse> updatePolicyPlan(@RequestBody @Valid PolicyPlanRequestDto requestDto, @PathVariable(name = "planId") String planId) {
        String uuid = serviceImpl.updatePolicyPlan(requestDto, planId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.toString(), "Policy Plan updated successfully for id " + uuid + " !!!", LocalDateTime.now()));
    }

    @PutMapping("/{planId}/deactivate")
    public ResponseEntity<ApiResponse> deactivatePolicyPlan(@PathVariable(name = "planId") String planId) {
        serviceImpl.deactivatePolicyPlan(planId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.toString(), "Policy Plan deactivated successfully with id " + planId + " !!!", LocalDateTime.now()));
    }

    @GetMapping
    public ResponseEntity<?> fetchAllActivePlans(@RequestParam(name = "page") int pageNumber, @RequestParam(name = "size") int pageSize, @RequestParam(name = "type", required = false) @ValidateInsuranceType String insurancePlan) {
        List<PolicyPlanResponseDto> responseDtoList = serviceImpl.activePolicyPlans(pageNumber, pageSize, insurancePlan);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDtoList);
    }
}
