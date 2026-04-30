package com.insurance.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_tbl")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    private UUID id;

    @Column(name = "keycloak_id", unique = true, updatable = false)
    private String keycloakId;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true, updatable = false)
    private String email;

    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Role role;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
