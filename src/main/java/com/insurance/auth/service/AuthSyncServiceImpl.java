package com.insurance.auth.service;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.dto.UserProfileUpdateRequest;
import com.insurance.auth.entity.User;
import com.insurance.auth.helper.AuthenticationHelper;
import com.insurance.auth.mapper.UserMapper;
import com.insurance.auth.repository.IUserRepository;
import com.insurance.common.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthSyncServiceImpl implements IAuthSyncService {
    private final IUserRepository userRepository;

    @Override
    public void syncUserToDb() {
        UserProfileDTO dto = AuthenticationHelper.getUserFromSecurityContext();

        // Check if user already exists based on the unique Keycloak ID
        Optional<User> existingUser = userRepository.findByKeycloakId(dto.getKeycloakId());

        if (existingUser.isPresent()) {
            // Update existing user (Sync latest name/email from Keycloak)
            User userToUpdate = existingUser.get();
            UserMapper.mapUserDtoToUser(dto, userToUpdate);
            userToUpdate.setUpdatedAt(LocalDate.now());
            userRepository.save(userToUpdate);
        } else {
            // Create new user only if they don't exist
            User newUser = new User();
            UserMapper.mapUserDtoToUser(dto, newUser);
            newUser.setId(UUID.randomUUID());
            newUser.setCreatedAt(LocalDate.now());
            userRepository.save(newUser);
        }
    }

    @Override
    public UserProfileDTO getUser() {
        String keycloakId = AuthenticationHelper.getLoggedInUserKeycloakId();
        Optional<User> existingUser = userRepository.findByKeycloakId(keycloakId);
        if (existingUser.isEmpty()) throw new ResourceNotFoundException("User", "Keycloak Id", keycloakId);
        UserProfileDTO userDto = new UserProfileDTO();
        UserMapper.mapUserToUserDto(userDto, existingUser.get());
        return userDto;
    }

    @Override
    public void updateUserDetails(UserProfileUpdateRequest updateRequest) {
        String keycloakId = AuthenticationHelper.getLoggedInUserKeycloakId();
        User existingUser = userRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new ResourceNotFoundException("User", "Keycloak Id", keycloakId));
        UserMapper.mapUserRequestToUser(updateRequest, existingUser);
        userRepository.save(existingUser);
    }
}
