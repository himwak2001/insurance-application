package com.insurance.auth.helper;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuthenticationHelper {
    public static UserProfileDTO getUserFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new RuntimeException("Unable to get authenticated user details!");
        }

        Map<String, Object> claims = jwt.getClaims();

        Role userRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(String::toUpperCase)
                .filter(role -> role.contains("ROLE_ADMIN") || role.contains("ROLE_CUSTOMER") || role.contains("ROLE_AGENT"))
                .findFirst()
                .map(AuthenticationHelper::mapToRoleEnum)
                .orElse(Role.CUSTOMER);

        return UserProfileDTO.builder()
                .keycloakId((String) claims.get("sub"))
                .fullName((String) claims.get("name"))
                .email((String) claims.get("email"))
                .role(userRole)
                .build();
    }

    public static String getLoggedInUserKeycloakId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new RuntimeException("Unable to get authenticated user details!");
        }

        Map<String, Object> claims = jwt.getClaims();

        return (String) claims.get("sub");
    }

    private static Role mapToRoleEnum(String authority) {
        String role = authority.replace("ROLE_", "");
        return Role.valueOf(role);
    }
}
