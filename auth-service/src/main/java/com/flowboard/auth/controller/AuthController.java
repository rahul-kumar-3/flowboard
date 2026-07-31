package com.flowboard.auth.controller;

import com.flowboard.auth.dto.request.ChangePasswordRequest;
import com.flowboard.auth.dto.request.LoginRequest;
import com.flowboard.auth.dto.request.RegisterRequest;
import com.flowboard.auth.dto.request.UpdateProfileRequest;
import com.flowboard.auth.dto.response.AuthResponse;
import com.flowboard.auth.dto.response.UserResponse;
import com.flowboard.auth.service.AuthService;
import com.flowboard.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User Registered Successfully")
                .data(authService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login Successful")
                .data(authService.login(request))
                .build();
    }

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(
            Authentication authentication) {

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile Retrieved Successfully")
                .data(authService.getProfile(authentication.getName()))
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(authentication.getName(), request);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Password Changed Successfully")
                .data("Password Updated")
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken(
            @RequestParam String refreshToken) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token Refreshed Successfully")
                .data(authService.refreshToken(refreshToken))
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(
            @RequestHeader("Authorization") String authorizationHeader) {

        authService.logout(authorizationHeader);

        return ApiResponse.<String>builder()
                .success(true)
                .message("Logout Successful")
                .data("Logged out successfully")
                .build();
    }

    @PutMapping("/profile")
    public ApiResponse<UserResponse> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile Updated Successfully")
                .data(authService.updateProfile(
                        authentication.getName(),
                        request))
                .build();
    }
}