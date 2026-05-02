package com.insurance.common.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    /**
     * Converts the roles from the Keycloak "realm_access" claim in the provided Jwt token
     * into a collection of Spring Security GrantedAuthority objects.
     *
     * @param source the Jwt token containing user claims, including Keycloak realm roles
     * @return a collection of GrantedAuthority objects mapped from Keycloak "roles" (with "ROLE_" prefix)
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }
        return ((List<String>) realmAccess.get("roles")).stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
