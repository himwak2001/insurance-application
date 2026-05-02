package com.insurance.auth.service;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.entity.Role;
import com.insurance.auth.entity.User;
import com.insurance.auth.helper.AuthenticationHelper;
import com.insurance.auth.mapper.UserMapper;
import com.insurance.auth.repository.IUserRepository;
import com.insurance.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthSyncServiceImplTest {
    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private AuthSyncServiceImpl syncService;

    private UserProfileDTO profileDTO;
    private String keycloakId;
    private UUID userId;
    private User user;
    private Role role;


    @BeforeEach
    void setup() {
        keycloakId = "dummy-keycloak-id";
        userId = UUID.randomUUID();
        role = Role.ADMIN;

        profileDTO = new UserProfileDTO(
                keycloakId,
                "Test Admin",
                "test_admin@test.com",
                role
        );


        user = new User(
                userId,
                keycloakId,
                "Test Admin",
                "test_admin@test.com",
                role
        );
    }

    @Test
    void testSyncUserToDb_NewUser_Success() {
        // arrange
        try (MockedStatic<AuthenticationHelper> mockedAuthenticationHelper = Mockito.mockStatic(AuthenticationHelper.class);
             MockedStatic<UserMapper> mockedUserMapper = Mockito.mockStatic(UserMapper.class)) {
            mockedAuthenticationHelper.when(AuthenticationHelper::getUserFromSecurityContext).thenReturn(profileDTO);
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(user);

            // act
            syncService.syncUserToDb();

            // assert
            assertNotNull(user);
            assertEquals(userId, user.getId());
            assertEquals(keycloakId, user.getKeycloakId());
            assertEquals("Test Admin", user.getFullName());
            assertEquals("test_admin@test.com", user.getEmail());
            verify(userRepository, times(1)).findByKeycloakId(keycloakId);
            verify(userRepository, times(1)).save(any(User.class));
            mockedAuthenticationHelper.verify(AuthenticationHelper::getUserFromSecurityContext);
            mockedUserMapper.verify(() -> UserMapper.mapUserDtoToUser(any(UserProfileDTO.class), any(User.class)));
        }
    }

    @Test
    void testSyncUserToDb_UserAlreadyExists_Success() {
        // arrange
        try (MockedStatic<AuthenticationHelper> mockedAuthenticationHelper = Mockito.mockStatic(AuthenticationHelper.class);
             MockedStatic<UserMapper> mockedUserMapper = Mockito.mockStatic(UserMapper.class)) {
            mockedAuthenticationHelper.when(AuthenticationHelper::getUserFromSecurityContext).thenReturn(profileDTO);
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // act
            syncService.syncUserToDb();

            // assert
            assertNotNull(user);
            assertEquals(userId, user.getId());
            assertEquals(keycloakId, user.getKeycloakId());
            assertEquals("Test Admin", user.getFullName());
            assertEquals("test_admin@test.com", user.getEmail());
            verify(userRepository, times(1)).findByKeycloakId(keycloakId);
            verify(userRepository, times(1)).save(any(User.class));
            mockedAuthenticationHelper.verify(AuthenticationHelper::getUserFromSecurityContext);
            mockedUserMapper.verify(() -> UserMapper.mapUserDtoToUser(any(UserProfileDTO.class), any(User.class)));
        }
    }

    @Test
    void testGetUser_UserExist_Success() throws ResourceNotFoundException {
        // arrange
        try (MockedStatic<AuthenticationHelper> mockedAuthenticationHelper = Mockito.mockStatic(AuthenticationHelper.class);
             MockedStatic<UserMapper> mockedUserMapper = Mockito.mockStatic(UserMapper.class)) {
            mockedAuthenticationHelper.when(AuthenticationHelper::getLoggedInUserKeycloakId).thenReturn(keycloakId);
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));

            // act
            syncService.getUser();

            // assert
            assertNotNull(user);
            assertEquals(userId, user.getId());
            assertEquals(keycloakId, user.getKeycloakId());
            assertEquals("Test Admin", user.getFullName());
            assertEquals("test_admin@test.com", user.getEmail());
            verify(userRepository, times(1)).findByKeycloakId(keycloakId);
            mockedAuthenticationHelper.verify(AuthenticationHelper::getLoggedInUserKeycloakId);
            mockedUserMapper.verify(() -> UserMapper.mapUserToUserDto(any(UserProfileDTO.class), any(User.class)));
        }
    }

    @Test
    void testGetUser_UserDoesNotExist_Failed() {
        // arrange
        try (MockedStatic<AuthenticationHelper> mockedAuthenticationHelper = Mockito.mockStatic(AuthenticationHelper.class);
             MockedStatic<UserMapper> mockedUserMapper = Mockito.mockStatic(UserMapper.class)) {
            mockedAuthenticationHelper.when(AuthenticationHelper::getLoggedInUserKeycloakId).thenReturn(keycloakId);
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());

            // act & assert
            assertThrows(ResourceNotFoundException.class, () -> syncService.getUser());
            mockedAuthenticationHelper.verify(AuthenticationHelper::getLoggedInUserKeycloakId);
            verify(userRepository, times(1)).findByKeycloakId(keycloakId);
        }
    }
}