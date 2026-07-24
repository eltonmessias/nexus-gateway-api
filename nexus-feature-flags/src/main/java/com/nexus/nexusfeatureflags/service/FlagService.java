package com.nexus.nexusfeatureflags.service;

import com.nexus.nexuscommons.dto.response.FlagEvaluateResponse;
import com.nexus.nexusfeatureflags.model.Flag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FlagService {
    FlagEvaluateResponse evaluate(String key);
    Flag create(Flag flag);
    Flag getById(UUID id);
    Flag update(UUID id, String description);
    Flag enable(UUID id);
    Flag disable(UUID id);
    void delete(UUID id);
    Page<Flag> findAll(Pageable pageable);
    Page<Flag> findAllByOrganizationId(UUID organizationId, Pageable pageable);
    Page<Flag> findAllByProjectId(UUID projectId, Pageable pageable);
}
