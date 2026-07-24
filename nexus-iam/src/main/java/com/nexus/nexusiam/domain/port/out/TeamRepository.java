package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(UUID id);
    List<Team> findByOrganizationId(UUID organizationId);
    List<Team> findByOrganizationId(UUID organizationId, int page, int size);
    long countByOrganizationId(UUID organizationId);
    List<Team> findAll();
    List<Team> findAll(int page, int size);
    long count();
    void deleteById(UUID id);
    boolean existsById(UUID id);
}
