package com.insurance.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.entity.Role;
import com.insurance.auth.service.AuthSyncServiceImpl;
import com.insurance.common.config.SecurityConfig;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@WebMvcTest(AuthSyncController.class)
class AuthSyncControllerTest {
    @MockitoBean
    private AuthSyncServiceImpl serviceImpl;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserProfileDTO profileDTO;
    private String keycloakId;
    private UUID userId;
    private Role role;

    @BeforeEach
    void setup() {
        keycloakId = "dummy-keycloak-id";
        userId = UUID.randomUUID();
        role = Role.ADMIN;

        profileDTO = new UserProfileDTO(
                userId,
                keycloakId,
                "Test Admin",
                "test_admin@test.com",
                role
        );
    }

    @Test
    void syncAuthenticatedUser_Success() throws Exception {
        // arrange
        doNothing().when(serviceImpl).syncUserToDb();

        // act & assert
        mockMvc.perform(post("/api/v1/sync")
                        .with(jwt()
                                .jwt(
                                        jwt -> jwt
                                                .claim("sub", "dummy-admin-001")
                                                .claim("preferred_username", "test_admin")
                                                .claim("email", "test_admin@test.com")
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_" + role)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200 OK"))
                .andExpect(jsonPath("$.message").value("User Sync Successfully"));
    }

    @Test
    void getUserById_Success() throws Exception {
        // arrange
        when(serviceImpl.getUser()).thenReturn(profileDTO);

        // act
        mockMvc.perform(get("/api/v1/me")
                        .with(jwt()
                                .jwt(
                                        jwt -> jwt
                                                .claim("sub", "dummy-admin-001")
                                                .claim("preferred_username", "test_admin")
                                                .claim("email", "test_admin@test.com")
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_" + role)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.email").value("test_admin@test.com"));

        // assert
        verify(serviceImpl).getUser();
    }
}