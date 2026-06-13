package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.in.TeamUseCase;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public Team create(Team team) {
        return teamRepository.save(team);
    }

    public Team update(UUID id, Team team) {
        teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
        return teamRepository.save(team);
    }

    public Team findById(UUID id) {
        return teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
    }

    public Team findByName(String name) {
        return teamRepository.findByName(name).orElse(null);
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public void delete(UUID id) {
        teamRepository.findById(id).orElseThrow(() -> new TeamNotFoundException(id));
        teamRepository.deleteById(id);
    }
}
