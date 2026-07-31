package com.flowboard.auth.service;

import com.flowboard.auth.dto.request.ChangePasswordRequest;
import com.flowboard.auth.dto.request.LoginRequest;
import com.flowboard.auth.dto.request.RegisterRequest;
import com.flowboard.auth.dto.request.UpdateProfileRequest;
import com.flowboard.auth.dto.response.AuthResponse;
import com.flowboard.auth.dto.response.UserResponse;

import java.util.UUID;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    Boolean validateToken(String token);

    UserResponse getUserByEmail(String email);

    UserResponse getUserById(UUID id);

    UserResponse getProfile(String email);

    void changePassword(String email, ChangePasswordRequest request);

    AuthResponse refreshToken(String refreshToken);

    void deactivateAccount(UUID id);

    void logout(String token);

    UserResponse updateProfile(String email, UpdateProfileRequest request);

}