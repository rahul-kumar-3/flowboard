package com.flowboard.auth.security.oauth;

import com.flowboard.auth.entity.User;
import com.flowboard.auth.enums.AuthProvider;
import com.flowboard.auth.repository.UserRepository;
import com.flowboard.common.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User =
                new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        OAuth2UserInfo userInfo;

        if ("google".equalsIgnoreCase(registrationId)) {

            userInfo = new GoogleOAuth2UserInfo(
                    oAuth2User.getAttributes());

        } else if ("github".equalsIgnoreCase(registrationId)) {

            userInfo = new GithubOAuth2UserInfo(
                    oAuth2User.getAttributes());

        } else {

            throw new OAuth2AuthenticationException(
                    "Unsupported OAuth Provider");

        }

        User user = userRepository
                .findByEmail(userInfo.getEmail())
                .orElseGet(() -> createUser(userInfo, registrationId));

        return new DefaultOAuth2User(

                Collections.singleton(() -> "ROLE_USER"),

                oAuth2User.getAttributes(),

                "email"

        );
    }

    private User createUser(OAuth2UserInfo userInfo,
                            String provider) {

        User user = User.builder()

                .email(userInfo.getEmail())

                .firstName(userInfo.getName())

                .avatarUrl(userInfo.getImageUrl())

                .provider(provider.equals("google")
                        ? AuthProvider.GOOGLE
                        : AuthProvider.GITHUB)

                .role(UserRole.MEMBER)

                .active(true)

                .build();

        return userRepository.save(user);

    }

}