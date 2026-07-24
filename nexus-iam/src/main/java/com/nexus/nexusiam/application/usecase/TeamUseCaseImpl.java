package com.nexus.nexusiam.application.usecase;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.in.TeamUseCase;
import com.nexus.nexusiam.domain.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamUseCaseImpl implements TeamUseCase {
    private final TeamService teamService;

    @Override
    public Team create(Team team) {
        Instant now = Instant.now();
        return teamService.create(Team.builder()
                .name(team.getName())
                .description(team.getDescription())
                .organizationId(team.getOrganizationId())
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());
    }

    @Override
    public Team update(UUID id, Team team) {
        return teamService.update(id, Team.builder()
                .id(id)
                .name(team.getName())
                .description(team.getDescription())
                .organizationId(team.getOrganizationId())
                .updatedAt(Instant.now())
                .active(team.isActive())
                .build());
    }

    @Override
    public Team findById(UUID id) {
        return teamService.findById(id);
    }

    @Override
    public PagedResult<Team> findAll(int page, int size) {
        return teamService.findAll(page, size);
    }

    @Override
    public PagedResult<Team> findByOrganizationId(UUID organizationId, int page, int size) {
        return teamService.findByOrganizationId(organizationId, page, size);
    }

    @Override
    public void delete(UUID id) {
        teamService.delete(id);
    }
}
