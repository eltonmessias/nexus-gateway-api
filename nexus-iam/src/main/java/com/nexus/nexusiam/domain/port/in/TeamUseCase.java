package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Team;

import java.util.UUID;

public interface TeamUseCase {
    Team create(Team team);
    Team update(UUID id, Team team);
    Team findById(UUID id);
    PagedResult<Team> findAll(int page, int size);
    PagedResult<Team> findByOrganizationId(UUID organizationId, int page, int size);
    void delete(UUID id);
}
