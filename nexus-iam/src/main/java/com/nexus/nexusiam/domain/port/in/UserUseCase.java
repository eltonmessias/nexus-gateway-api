package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.User;

import java.util.UUID;

public interface UserUseCase {
    User create(User user);
    User update(UUID id, User user);
    User findById(UUID id);
    PagedResult<User> findAll(int page, int size);
    void delete(UUID id);
    User setActive(UUID id, boolean active);
}
