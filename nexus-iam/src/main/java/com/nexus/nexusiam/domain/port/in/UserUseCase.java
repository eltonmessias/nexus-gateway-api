package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexusiam.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {
    User create(User user);
    User update(UUID id, User user);
    User findById(UUID id);
    List<User> findAll();
    void delete(UUID id);
}
