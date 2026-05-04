package com.insurance.policy.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "policy_plan_tbl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PolicyPlan {
    @Id
    private UUID id;

    @Column(name = "plan_name", length = 50)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", length = 20)
    private InsuranceType insuranceType;

    @Column(name = "coverage_amount", precision = 12, scale = 2)
    private BigDecimal coverageAmount;

    @Column(name = "base_premium", precision = 12, scale = 2)
    private BigDecimal basePremium;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "exclusions")
    private String exclusions;

    @Column(name = "is_active")
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolicyPlan that = (PolicyPlan) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
