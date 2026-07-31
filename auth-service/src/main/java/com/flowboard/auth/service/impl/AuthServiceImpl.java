package com.flowboard.auth.service.impl;

import com.flowboard.auth.dto.request.ChangePasswordRequest;
import com.flowboard.auth.dto.request.LoginRequest;
import com.flowboard.auth.dto.request.RegisterRequest;
import com.flowboard.auth.dto.request.UpdateProfileRequest;
import com.flowboard.auth.dto.response.AuthResponse;
import com.flowboard.auth.dto.response.UserResponse;
import com.flowboard.auth.entity.User;
import com.flowboard.auth.enums.AuthProvider;
import com.flowboard.auth.repository.UserRepository;
import com.flowboard.auth.security.service.JwtService;
import com.flowboard.auth.service.AuthService;
import com.flowboard.common.enums.UserRole;
import com.flowboard.common.exception.BadRequestException;
import com.flowboard.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (request.getMobile() != null &&
                userRepository.existsByMobile(request.getMobile())) {

            throw new BadRequestException("Mobile already exists");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new BadRequestException("Username already exists");
        }

        User user = User.builder()

                .firstName(request.getFirstName())

                .lastName(request.getLastName())

                .fullName(request.getFirstName()+" "+request.getLastName())

                .email(request.getEmail())

                .username(request.getUsername())

                .password(passwordEncoder.encode(request.getPassword()))

                .provider(AuthProvider.LOCAL)

                .role(UserRole.MEMBER)

                .avatarUrl("https://www.flaticon.com/free-icon/user_709722?term=avatar&page=1&position=47&origin=tag&related_id=709722")

                .build();

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }



    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        if (user.getProvider() != AuthProvider.LOCAL) {

            throw new BadRequestException(
                    "Please login using " + user.getProvider().name());

        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        String accessToken =
                jwtService.generateAccessToken(user.getEmail());

        String refreshToken =
                jwtService.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    @Override
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        return mapToUserResponse(user);
    }

    @Override
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        if (!passwordEncoder.matches(
                request.getOldPassword(),
                user.getPassword())) {

            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);
    }

    @Override
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        user.setFirstName(request.getFirstName());

        user.setLastName(request.getLastName());

        user.setUsername(request.getUsername());

        user.setMobile(request.getMobile());


        user.setAvatarUrl(request.getAvatarUrl());

        userRepository.save(user);

        return mapToUserResponse(user);
    }

    @Override
    public Boolean validateToken(String token) {
        return jwtService.isTokenValid(token, jwtService.extractUsername(token));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()->  new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
    }

    @Override
    public void deactivateAccount(UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        user.setActive(false);

        userRepository.save(user);
    }

    @Override
    public void logout(String token) {

        if (token == null || token.isBlank()) {
            throw new BadRequestException("Authorization token is required");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtService.extractUsername(token);

        if (email == null || !jwtService.isTokenValid(token, email)) {
            throw new BadRequestException("Invalid or expired token");
        }

    }


    @Override
    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BadRequestException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, email)) {
            throw new BadRequestException("Invalid Refresh Token");
        }

        String newAccessToken =
                jwtService.generateAccessToken(email);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .active(user.getActive())
                .build();
    }
}