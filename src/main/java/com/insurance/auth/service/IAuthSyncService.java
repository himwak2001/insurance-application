package com.insurance.auth.service;

import com.insurance.auth.dto.UserProfileDTO;
import com.insurance.auth.dto.UserProfileUpdateRequest;

public interface IAuthSyncService {
    void syncUserToDb();

    UserProfileDTO getUser();

    void updateUserDetails(UserProfileUpdateRequest updateRequest);
}
