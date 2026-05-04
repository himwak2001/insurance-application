package com.insurance.policy.repository;

import com.insurance.policy.dto.PolicyPlanRequestDto;
import com.insurance.policy.dto.PolicyPlanResponseDto;
import com.insurance.policy.entity.InsuranceType;
import com.insurance.policy.entity.PolicyPlan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IPolicyPlanRepository extends JpaRepository<PolicyPlan, UUID>, JpaSpecificationExecutor<PolicyPlan> {
    Optional<PolicyPlan> findByPlanName(String planName);

    @Query("SELECT new com.insurance.policy.dto.PolicyPlanResponseDto(p.id, p.planName, p.insuranceType, p.coverageAmount, p.basePremium, p.durationMonths, p.minAge, p.maxAge, p.exclusions) FROM PolicyPlan p WHERE p.isActive=true and p.insuranceType=:type")
    List<PolicyPlanResponseDto> findAllActivePlans(Pageable pageable, @Param("type") InsuranceType type);
}
