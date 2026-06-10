package com.nexus.nexusfeatureflags.repository;

import com.nexus.nexusfeatureflags.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface FlagRepository extends JpaRepository<Flag, UUID> {
    Optional<Flag> findByFlagKeyAndEnvironment(String flagKey, String environment);

}
