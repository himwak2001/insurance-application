package com.insurance.auth.controller;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.dto.UserProfileUpdateRequest;
import com.insurance.auth.service.AuthSyncServiceImpl;
import com.insurance.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final AuthSyncServiceImpl serviceImpl;

    @GetMapping
    public ResponseEntity<UserProfileDTO> getLoggedInUserProfile() {
        UserProfileDTO dto = serviceImpl.getUser();
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateUserDetails(@RequestBody UserProfileUpdateRequest updateRequest) {
        serviceImpl.updateUserDetails(updateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse(HttpStatus.OK.toString(), "User updated successfully!", LocalDateTime.now()));
    }
}
