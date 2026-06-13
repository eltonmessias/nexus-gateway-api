package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository {
    Team save(Team team);
    Optional<Team> findById(UUID id);
    Optional<Team> findByOrganizationId(UUID organizationId);
    Optional<Team> findByName(String name);
    List<Team> findAll();
    void deleteById(UUID id);
}
