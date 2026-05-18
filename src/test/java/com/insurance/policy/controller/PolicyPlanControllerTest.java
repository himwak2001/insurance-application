package com.insurance.policy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.common.config.SecurityConfig;
import com.insurance.policy.dto.PolicyPlanRequestDto;
import com.insurance.policy.dto.PolicyPlanResponseDto;
import com.insurance.policy.entity.InsuranceType;
import com.insurance.policy.service.PolicyPlanServiceImpl;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(PolicyPlanController.class)
@DisplayName("Policy Plan Controller Tests")
class PolicyPlanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PolicyPlanServiceImpl serviceImpl;

    private PolicyPlanResponseDto responseDto;
    private PolicyPlanRequestDto validRequestDto;
    private String planId;

    @BeforeEach
    void setup() {
        planId = UUID.randomUUID().toString();

        validRequestDto = new PolicyPlanRequestDto(
                "Super Health Plan", "HEALTH", new BigDecimal("500000"),
                new BigDecimal("500"), 12, 18, 60, "Pre-existing conditions"
        );

        responseDto = new PolicyPlanResponseDto(
                UUID.fromString(planId), "Super Health Plan", InsuranceType.HEALTH,
                new BigDecimal("500000"), new BigDecimal("500"), 12, 18, 60, "Pre-existing conditions"
        );
    }

    @Test
    @DisplayName("POST /api/v1/policy-plans - Should create policy and return 201 Created")
    void createPolicyPlan_ReturnsCreated() throws Exception {
        // given
        willDoNothing().given(serviceImpl).createPolicyPlan(any(PolicyPlanRequestDto.class));

        // when / then
        mockMvc.perform(post("/api/v1/policy-plans")
                        .with(jwt()
                                .jwt(
                                        jwt -> jwt
                                                .claim("sub", "dummy-admin-001")
                                                .claim("preferred_username", "test_admin")
                                                .claim("email", "test_admin@test.com")
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Policy Plan created successfully !!!"))
                .andExpect(jsonPath("$.status").value("201 CREATED"));
    }

    @Test
    @DisplayName("PUT /api/v1/policy-plans/{planId} - Should update policy and return 200 OK")
    void updatePolicyPlan_ReturnsOk() throws Exception {
        // Given
        given(serviceImpl.updatePolicyPlan(any(PolicyPlanRequestDto.class), eq(planId)))
                .willReturn(planId);

        // when / then
        mockMvc.perform(put("/api/v1/policy-plans/{planId}", planId)
                        .with(jwt()
                                .jwt(
                                        jwt -> jwt
                                                .claim("sub", "dummy-admin-001")
                                                .claim("preferred_username", "test_admin")
                                                .claim("email", "test_admin@test.com")
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Policy Plan updated successfully for id " + planId + " !!!"));
    }

    @Test
    @DisplayName("PUT /api/v1/policy-plans/{planId}/deactivate - Should deactivate policy and return 200 OK")
    void deactivatePolicyPlan_ReturnsOk() throws Exception {
        // Given
        willDoNothing().given(serviceImpl).deactivatePolicyPlan(planId);

        // When & Then
        mockMvc.perform(put("/api/v1/policy-plans/{planId}/deactivate", planId)
                        .with(jwt()
                                .jwt(
                                        jwt -> jwt
                                                .claim("sub", "dummy-admin-001")
                                                .claim("preferred_username", "test_admin")
                                                .claim("email", "test_admin@test.com")
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Policy Plan deactivated successfully with id " + planId + " !!!"));
    }

    @Test
    @DisplayName("GET /api/v1/policy-plans - Should return paginated list and 200 OK")
    void fetchAllActivePlans_ReturnsList() throws Exception {
        // Given
        List<PolicyPlanResponseDto> dtoList = List.of(responseDto);
        given(serviceImpl.activePolicyPlans(0, 5, "HEALTH")).willReturn(dtoList);

        // when / then
        mockMvc.perform(get("/api/v1/policy-plans")
                .param("page", "0")
                .param("size", "5")
                .param("type", "HEALTH")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].planName").value("Super Health Plan"));
    }

    @Test
    @DisplayName("GET /api/v1/policy-plans/{planId} - Should return policy by ID and 200 OK")
    void findPolicyPlanById_ReturnsPolicy() throws Exception {
        // Given
        given(serviceImpl.getPolicyPlan(planId)).willReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/v1/policy-plans/{planId}", planId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planId))
                .andExpect(jsonPath("$.planName").value("Super Health Plan"));
    }
}