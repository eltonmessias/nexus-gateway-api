package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.port.in.UserUseCase;
import com.nexus.nexusiam.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserUseCaseImpl implements UserUseCase {
    private final UserService userService;

    @Override
    public User create(User user) {
        return userService.create(user);
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
    public List<User> findAll() {
        return userService.findAll();
    }

    @Override
    public void delete(UUID id) {
        userService.delete(id);
    }
}
