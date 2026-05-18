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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName(value = "Policy Plan Service Unit Tests")
class PolicyPlanServiceImplTest {
    @Mock
    private IPolicyPlanRepository policyPlanRepository;

    @Mock
    private PolicyPlanMapper policyPlanMapper;

    @Mock
    private ValidatePolicyPlan validatePolicyPlan;

    @InjectMocks
    private PolicyPlanServiceImpl policyPlanService;

    private UUID policyPlanId;
    private PolicyPlanResponseDto responseDto;
    private PolicyPlanRequestDto requestDto;
    private PolicyPlan policyPlan;

    @BeforeEach
    void setUp() {
        policyPlanId = UUID.randomUUID();

        requestDto = new PolicyPlanRequestDto(
                "Standard Health", "HEALTH", new BigDecimal("500000"),
                new BigDecimal("500"), 12, 21, 60, "None"
        );

        policyPlan = new PolicyPlan(
                policyPlanId, "Standard Health", InsuranceType.HEALTH,
                new BigDecimal("500000"), new BigDecimal("500"), 12, 21, 60, "None"
        );
    }

    @Nested
    @DisplayName("Creation Logic")
    class CreatePolicyPlan {
        @Test
        @DisplayName("Should save policy successfully when request is valid and name is unique")
        void createPolicyPlan_whenPlanDoesNotExist_shouldSavePolicyPlan() {
            // given
            given(validatePolicyPlan.validatePolicyPlan(requestDto)).willReturn(List.of());
            given(policyPlanRepository.findByPlanName(anyString())).willReturn(Optional.empty());
            given(policyPlanRepository.save(any(PolicyPlan.class))).willReturn(policyPlan);

            // when
            policyPlanService.createPolicyPlan(requestDto);

            // then
            then(policyPlanRepository).should().save(any(PolicyPlan.class));
            then(policyPlanRepository).should().findByPlanName(requestDto.getPlanName());
        }

        @Test
        @DisplayName("Should throw ResourceAlreadyExistException when plan name already exists")
        void createPolicyPlan_whenPlanDoesExist_shouldThrowException() {
            // given
            given(policyPlanRepository.findByPlanName(anyString())).willReturn(Optional.of(policyPlan));

            // when / then
            assertThatThrownBy(() -> policyPlanService.createPolicyPlan(requestDto)).isInstanceOf(ResourceAlreadyExistException.class);
            then(policyPlanRepository).should(never()).save(any(PolicyPlan.class));
        }

        @Test
        @DisplayName("Should throw PolicyValidationException when validation rules fail")
        void createPolicyPlan_whenPlanIsInvalid_shouldThrowException() {
            // given
            given(validatePolicyPlan.validatePolicyPlan(requestDto)).willReturn(List.of("Invalid Coverage"));

            // when / then
            assertThatThrownBy(() -> policyPlanService.createPolicyPlan(requestDto)).isInstanceOf(PolicyValidationException.class);
        }
    }

    @Nested
    @DisplayName("Update & Deactivate Logic")
    class updateDeactivatePolicy {
        @Test
        @DisplayName("Should update existing policy plan")
        void updatePolicyPlan_whenPlanExists_shouldUpdatePolicyPlan() {
            // given
            given(policyPlanRepository.findById(policyPlanId)).willReturn(Optional.of(policyPlan));
            given(policyPlanRepository.save(policyPlan)).willReturn(policyPlan);

            // when
            policyPlanService.updatePolicyPlan(requestDto, policyPlanId.toString());

            // then
            then(policyPlanMapper).should().mapPolicyPlanRequestDtoToPolicyPlan(requestDto, policyPlan, true);
            then(policyPlanRepository).should().save(policyPlan);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
        void updatePolicyPlan_whenPlanDoesNotExists_shouldUpdatePolicyPlan() {
            // given
            given(policyPlanRepository.findById(policyPlanId)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> policyPlanService.updatePolicyPlan(requestDto, policyPlanId.toString())).isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should deactivate policy by setting isActive to false")
        void deactivatePolicyPlan_whenPlanDoesExists_shouldDeactivatePolicyPlan() {
            // given
            given(policyPlanRepository.findById(policyPlanId)).willReturn(Optional.of(policyPlan));

            // when
            policyPlanService.deactivatePolicyPlan(policyPlanId.toString());

            // then
            assertThat(policyPlan.getIsActive()).isFalse();
            then(policyPlanRepository).should().save(policyPlan);
        }
    }

    @Nested
    @DisplayName("Retrieval Logic")
    class RetrievalLogic{
        @Test
        void activePolicyPlans_shouldReturnListOfPolicyPlanResponseDto() {
            // given
            var pageable = PageRequest.of(0, 5);
            given(policyPlanRepository.findAllActivePlans(pageable, InsuranceType.HEALTH)).willReturn(List.of());

            // when
            policyPlanService.activePolicyPlans(0, 5, "HEALTH");

            // then
            then(policyPlanRepository).should().findAllActivePlans(pageable, InsuranceType.HEALTH);
        }

        @Test
        void getPolicyPlan_whenPlanDoesExists_shouldReturnPolicyPlanResponseDto() {
            // given
            given(policyPlanRepository.findById(policyPlanId)).willReturn(Optional.of(policyPlan));

            // when
            policyPlanService.getPolicyPlan(policyPlanId.toString());;

            // then
            then(policyPlanMapper).should().mapPolicyPlanToPolicyPlanResponseDto(any(PolicyPlanResponseDto.class), eq(policyPlan));
            then(policyPlanRepository).should().findById(policyPlanId);
        }
    }
}