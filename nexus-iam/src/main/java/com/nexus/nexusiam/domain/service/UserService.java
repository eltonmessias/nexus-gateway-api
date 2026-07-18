package com.nexus.nexusiam.domain.service;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.exception.UserNotFoundException;
import com.nexus.nexusiam.domain.model.User;
import com.nexus.nexusiam.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public User update(UUID id, User user) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userRepository.save(user);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public PagedResult<User> findAll(int page, int size) {
        List<User> content = userRepository.findAll(page, size);
        long total = userRepository.count();
        return PagedResult.of(content, page, size, total);
    }

    public void delete(UUID id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);
    }
}
