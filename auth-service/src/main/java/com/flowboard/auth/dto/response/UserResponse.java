package com.flowboard.auth.dto.response;

import com.flowboard.common.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserResponse {

    private UUID id;

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String mobile;

    private UserRole role;

    private Boolean active;

    private String avatarUrl;

}