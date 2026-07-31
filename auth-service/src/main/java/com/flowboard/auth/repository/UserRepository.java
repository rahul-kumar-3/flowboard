package com.flowboard.auth.repository;

import com.flowboard.auth.entity.User;
import com.flowboard.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findById(UUID id);

    Boolean existsByEmail(String email);

    Boolean existsByUsername(String username);

    List<User> findAllByRole(UserRole role);

    List<User> searchByFullName(String fullName);

    Boolean existsByMobile(String mobile);
}
