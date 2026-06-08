package com.nexus.nexusfeatureflags.service;

import com.nexus.nexuscommons.dto.response.FlagEvaluateResponse;
import com.nexus.nexusfeatureflags.model.Flag;

import java.util.List;
import java.util.UUID;

public interface FlagService {

    FlagEvaluateResponse evaluate(String flagKey, String environment);
    Flag create(Flag flag);
    Flag getById(UUID id);
    Flag update(UUID id, Flag flag);
    void delete(UUID id);
    List<Flag> findAll();
}
