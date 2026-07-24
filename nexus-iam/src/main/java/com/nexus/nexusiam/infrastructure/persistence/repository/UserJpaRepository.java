package com.nexus.nexusiam.infrastructure.persistence.repository;

import com.nexus.nexusiam.domain.model.Role;
import com.nexus.nexusiam.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByName(String name);
    List<UserEntity> findAllByOrganizationId(UUID organizationId);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
}
