package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findByName(String name);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    List<User> findAllByOrganizationId(UUID organizationId);
    List<User> findAll();
    void deleteById(UUID id);
    boolean existsById(UUID id);

    boolean existsByEmail(String email);
}
