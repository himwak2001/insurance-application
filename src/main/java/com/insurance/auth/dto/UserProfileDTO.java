package com.insurance.auth.dto;

import com.insurance.auth.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserProfileDTO {
    private UUID id;
    private String keycloakId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String address;
    private Role role;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public UserProfileDTO(UUID id, String keycloakId, String fullName, String email, Role role) {
        this.id = id;
        this.keycloakId = keycloakId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public UserProfileDTO(String keycloakId, String fullName, String email, Role role) {
        this.keycloakId = keycloakId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }
}
