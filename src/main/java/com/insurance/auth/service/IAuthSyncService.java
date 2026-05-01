package com.insurance.auth.service;

import com.insurance.auth.dto.UserProfileDTO;

public interface IAuthSyncService {
    void syncUserToDb();

    UserProfileDTO getUser();
}
