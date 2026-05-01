package com.insurance.auth.controller;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.service.AuthSyncServiceImpl;
import com.insurance.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class AuthSyncController {
    private final AuthSyncServiceImpl authSyncService;

    @PostMapping("/sync")
    public ResponseEntity<ApiResponse> syncAuthenticatedUser() {
        authSyncService.syncUserToDb();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.toString(), "User Sync Successfully", LocalDateTime.now()));
    }

    @GetMapping(path = "/me")
    public ResponseEntity<UserProfileDTO> getUserById() {
        UserProfileDTO dto = authSyncService.getUser();
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(dto);
    }
}
