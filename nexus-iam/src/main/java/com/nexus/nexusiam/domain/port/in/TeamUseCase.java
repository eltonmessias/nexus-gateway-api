package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexusiam.domain.model.Team;

import java.util.List;
import java.util.UUID;

public interface TeamUseCase {
    Team create(Team team);
    Team update(UUID id, Team team);
    Team findById(UUID id);
    Team findByName(String name);
    List<Team> findAll();
    void delete(UUID id);

}
