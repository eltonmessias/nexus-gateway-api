package com.nexus.nexusiam.application.usecase;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Role;
import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.port.in.UserUseCase;
import com.nexus.nexusiam.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserUseCaseImpl implements UserUseCase {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {
        Instant now = Instant.now();
        User userWithHashedPassword = User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(passwordEncoder.encode(user.getPasswordHash()))
                .organizationId(user.getOrganizationId())
                .role(user.getRole() != null ? user.getRole() : Role.VIEWER)
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build();
        return userService.create(userWithHashedPassword);
    }

    @Override
    public User update(UUID id, User user) {
        return userService.update(id, user);
    }

    @Override
    public User findById(UUID id) {
        return userService.findById(id);
    }

    @Override
    public PagedResult<User> findAll(int page, int size) {
        return userService.findAll(page, size);
    }

    @Override
    public void delete(UUID id) {
        userService.delete(id);
    }

    @Override
    public User setActive(UUID id, boolean active) {
        return userService.setActive(id, active);
    }
}
