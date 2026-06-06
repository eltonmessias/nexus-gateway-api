package com.nexus.nexusfeatureflags.repository;

import com.nexus.nexusfeatureflags.model.Flag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlagRepository {
    Flag save(Flag flag);
    Optional<Flag> findById(UUID id);
    Optional<Flag> findByFlagKeyAndEnvironment(String flagKey, String environment);
    List<Flag> findAll();
    void deleteById(UUID id);

}
