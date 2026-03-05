package com.guitar.service;

import com.guitar.dto.request.LoginRequest;
import com.guitar.dto.request.RefreshTokenRequest;
import com.guitar.dto.request.RegisterRequest;
import com.guitar.dto.request.UpdateProfileRequest;
import com.guitar.dto.response.LoginResponse;
import com.guitar.dto.response.UserInfoResponse;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(Long userId);

    UserInfoResponse getCurrentUser(Long userId);

    void updateProfile(Long userId, UpdateProfileRequest request);
}
