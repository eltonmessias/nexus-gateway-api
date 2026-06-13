package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findByTeamId(UUID teamId);
    List<User> findAll();
    void deleteById(UUID id);
}
