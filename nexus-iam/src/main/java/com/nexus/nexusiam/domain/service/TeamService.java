package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.in.TeamUseCase;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TeamService implements TeamUseCase {
    private final TeamRepository teamRepository;

    @Override
    public Team create(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public Team update(UUID id, Team team) {
        teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
        return teamRepository.save(team);
    }

    @Override
    public Team findById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
    }

    @Override
    public Team findByName(String name) {
        return teamRepository.findByName(name).orElse(null);
    }

    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
        teamRepository.deleteById(id);
    }
}
