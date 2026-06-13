package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.OrganizationNotFoundException;
import com.nexus.nexusiam.domain.exception.UserNotFoundException;
import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.port.in.UserUseCase;
import com.nexus.nexusiam.domain.port.out.OrganizationRepository;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(UUID id, User user) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
