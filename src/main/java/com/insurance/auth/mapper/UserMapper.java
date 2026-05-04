package com.insurance.auth.mapper;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.dto.UserProfileUpdateRequest;
import com.insurance.auth.entity.User;

public class UserMapper {
    public static void mapUserDtoToUser(UserProfileDTO dto, User user) {
        user.setKeycloakId(dto.getKeycloakId());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole());
    }

    public static void mapUserToUserDto(UserProfileDTO dto, User user) {
        dto.setId(user.getId());
        dto.setKeycloakId(user.getKeycloakId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
    }

    public static void mapUserRequestToUser(UserProfileUpdateRequest request, User existingUser) {
        existingUser.setPhone(request.getPhone());
        existingUser.setAddress(request.getAddress());
        existingUser.setFullName(request.getFullName());
    }
}
