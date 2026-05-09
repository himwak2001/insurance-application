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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyPlanServiceImplTest {
    @Mock
    private IPolicyPlanRepository policyPlanRepository;

    @InjectMocks
    private PolicyPlanServiceImpl policyPlanService;

    private PolicyPlanResponseDto responseDto;
    private PolicyPlanRequestDto requestDto;
    private PolicyPlan updatedPolicyPlan;
    private InsuranceType insuranceType;
    private PolicyPlan savedPolicyPlan;
    private PolicyPlan newPolicyPlan;
    private List<String> validList;
    private List<String> inValidList;
    private UUID policyPlanId;


    @BeforeEach
    void setUp() {
        policyPlanId = UUID.randomUUID();
        insuranceType = InsuranceType.HEALTH;

        requestDto = new PolicyPlanRequestDto(
                "dummy_plan_name",
                insuranceType.name(),
                new BigDecimal(500000),
                new BigDecimal(500),
                12,
                21,
                60,
                "dummy_exclusions"
        );

        responseDto = new PolicyPlanResponseDto(
                policyPlanId,
                "dummy_plan_name",
                insuranceType,
                new BigDecimal(500000),
                new BigDecimal(500),
                12,
                21,
                60,
                "dummy_exclusions"
        );

        savedPolicyPlan = new PolicyPlan(
                policyPlanId,
                "dummy_plan_name",
                insuranceType,
                new BigDecimal(500000),
                new BigDecimal(500),
                12,
                21,
                60,
                "dummy_exclusions"
        );

        updatedPolicyPlan = new PolicyPlan(
                policyPlanId,
                "dummy_plan_updated",
                insuranceType,
                new BigDecimal(600000),
                new BigDecimal(1000),
                18,
                21,
                60,
                "dummy_exclusions_updated"
        );

        newPolicyPlan = new PolicyPlan(
                "dummy_plan_name",
                insuranceType,
                new BigDecimal(500000),
                new BigDecimal(500),
                12,
                21,
                60,
                "dummy_exclusions"
        );

        validList = new ArrayList<>();

        inValidList = new ArrayList<>(
                List.of("Coverage Amount must be less than Base Premium!", "Minimum Age must be less than Maximum Age!")
        );
    }

    @Test
    void createPolicyPlan_whenPlanDoesNotExist_shouldSavePolicyPlan() {
        // arrange
        try (MockedStatic<ValidatePolicyPlan> validatePolicyPlanMockedStatic = Mockito.mockStatic(ValidatePolicyPlan.class);
             MockedStatic<PolicyPlanMapper> policyPlanMapperMockedStatic = Mockito.mockStatic(PolicyPlanMapper.class)) {
            validatePolicyPlanMockedStatic.when(() -> ValidatePolicyPlan.validatePolicyPlan(requestDto)).thenReturn(validList);
            when(policyPlanRepository.findByPlanName(requestDto.getPlanName())).thenReturn(Optional.empty());
            when(policyPlanRepository.save(any(PolicyPlan.class))).thenReturn(savedPolicyPlan);

            // act
            policyPlanService.createPolicyPlan(requestDto);

            // assert
            assertNotNull(savedPolicyPlan);
            assertEquals(requestDto.getPlanName(), savedPolicyPlan.getPlanName());
            assertEquals(policyPlanId, savedPolicyPlan.getId());
            assertEquals(validList.size(), 0);
            verify(policyPlanRepository, times(1)).save(any(PolicyPlan.class));
            verify(policyPlanRepository, times(1)).findByPlanName(requestDto.getPlanName());
            validatePolicyPlanMockedStatic.verify(() -> ValidatePolicyPlan.validatePolicyPlan(any(PolicyPlanRequestDto.class)));
            policyPlanMapperMockedStatic.verify(() -> PolicyPlanMapper.mapPolicyPlanRequestDtoToPolicyPlan(eq(requestDto), any(PolicyPlan.class), eq(false)));
        }
    }

    @Test
    void createPolicyPlan_whenPlanDoesExist_shouldThrowException(){
        try (MockedStatic<ValidatePolicyPlan> validatePolicyPlanMockedStatic = Mockito.mockStatic(ValidatePolicyPlan.class);
             MockedStatic<PolicyPlanMapper> policyPlanMapperMockedStatic = Mockito.mockStatic(PolicyPlanMapper.class)) {
            when(policyPlanRepository.findByPlanName(requestDto.getPlanName())).thenReturn(Optional.of(savedPolicyPlan));

            // act & assert
            assertThrows(ResourceAlreadyExistException.class, () -> policyPlanService.createPolicyPlan(requestDto));
            verify(policyPlanRepository, times(1)).findByPlanName(requestDto.getPlanName());
        }
    }

    @Test
    void createPolicyPlan_whenPlanIsInvalid_shouldThrowException(){
        try (MockedStatic<ValidatePolicyPlan> validatePolicyPlanMockedStatic = Mockito.mockStatic(ValidatePolicyPlan.class);
             MockedStatic<PolicyPlanMapper> policyPlanMapperMockedStatic = Mockito.mockStatic(PolicyPlanMapper.class)) {
            validatePolicyPlanMockedStatic.when(() -> ValidatePolicyPlan.validatePolicyPlan(requestDto)).thenReturn(inValidList);
            when(policyPlanRepository.findByPlanName(requestDto.getPlanName())).thenReturn(Optional.empty());

            // act & assert
            assertThrows(PolicyValidationException.class, () -> policyPlanService.createPolicyPlan(requestDto));
            verify(policyPlanRepository, times(1)).findByPlanName(requestDto.getPlanName());
            validatePolicyPlanMockedStatic.verify(() -> ValidatePolicyPlan.validatePolicyPlan(any(PolicyPlanRequestDto.class)));
        }
    }

    @Test
    void updatePolicyPlan_whenPlanExists_shouldUpdatePolicyPlan() {
        // arrange
        try(MockedStatic<PolicyPlanMapper> policyPlanMapperMockedStatic = Mockito.mockStatic(PolicyPlanMapper.class)){
            when(policyPlanRepository.findById(policyPlanId)).thenReturn(Optional.of(savedPolicyPlan));
            when(policyPlanRepository.save(savedPolicyPlan)).thenReturn(updatedPolicyPlan);

            // act
            policyPlanService.updatePolicyPlan(requestDto, policyPlanId.toString());

            // assert
            assertNotNull(savedPolicyPlan);
            assertEquals("dummy_plan_updated", updatedPolicyPlan.getPlanName());
            assertEquals(new BigDecimal(600000), updatedPolicyPlan.getCoverageAmount());
            assertEquals(new BigDecimal(1000), updatedPolicyPlan.getBasePremium());
            assertEquals(18, updatedPolicyPlan.getDurationMonths());
            verify(policyPlanRepository, times(1)).findById(policyPlanId);
            verify(policyPlanRepository, times(1)).save(savedPolicyPlan);
            policyPlanMapperMockedStatic.verify(() -> PolicyPlanMapper.mapPolicyPlanRequestDtoToPolicyPlan(requestDto, savedPolicyPlan, true));
        }
    }

    @Test
    void updatePolicyPlan_whenPlanDoesNotExists_shouldUpdatePolicyPlan(){
        // arrange
        try(MockedStatic<PolicyPlanMapper> policyPlanMapperMockedStatic = Mockito.mockStatic(PolicyPlanMapper.class)){
            when(policyPlanRepository.findById(policyPlanId)).thenReturn(Optional.empty());

            // act & assert
            assertThrows(ResourceNotFoundException.class, () -> policyPlanService.updatePolicyPlan(requestDto, policyPlanId.toString()));
            verify(policyPlanRepository, times(1)).findById(policyPlanId);
        }
    }

    @Test
    void deactivatePolicyPlan() {
    }

    @Test
    void activePolicyPlans() {
    }

    @Test
    void getPolicyPlan() {
    }
}