package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.in.TeamUseCase;
import com.nexus.nexusiam.domain.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamUseCaseImpl implements TeamUseCase {
    private final TeamService teamService;
    @Override
    public Team create(Team team) {
        return teamService.create(team);
    }

    @Override
    public Team update(UUID id, Team team) {
        return teamService.update(id, team);
    }

    @Override
    public Team findById(UUID id) {
        return teamService.findById(id);
    }

    @Override
    public Team findByName(String name) {
        return teamService.findByName(name);
    }

    @Override
    public List<Team> findAll() {
        return teamService.findAll();
    }

    @Override
    public void delete(UUID id) {
        teamService.delete(id);
    }
}
