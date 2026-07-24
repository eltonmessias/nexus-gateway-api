package com.nexus.nexusfeatureflags.service;

import com.nexus.nexuscachelayer.service.CacheService;
import com.nexus.nexuscachelayer.util.CacheKeyUtil;
import com.nexus.nexuscommons.dto.response.FlagEvaluateResponse;
import com.nexus.nexuscommons.exception.FlagNotFoundException;
import com.nexus.nexusfeatureflags.model.Flag;
import com.nexus.nexusfeatureflags.repository.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class FlagServiceImpl implements FlagService {
    private final FlagRepository flagRepository;
    private final CacheService cacheService;

    @Autowired
    public FlagServiceImpl(FlagRepository flagRepository, CacheService cacheService) {
        this.flagRepository = flagRepository;
        this.cacheService = cacheService;
    }

    @Override
    public FlagEvaluateResponse evaluate(String key) {
        Optional<Object> cached = cacheService.get(CacheKeyUtil.forFlag(key));
        if (cached.isPresent()) {
            Flag flag = (Flag) cached.get();
            return new FlagEvaluateResponse(flag.getKey(), flag.isEnabled(),
                    flag.isEnabled() ? "FLAG_ENABLED" : "FLAG_DISABLED");
        }

        return flagRepository.findByKey(key)
                .map(flag -> {
                    cacheService.set(CacheKeyUtil.forFlag(key), flag);
                    return new FlagEvaluateResponse(flag.getKey(), flag.isEnabled(),
                            flag.isEnabled() ? "FLAG_ENABLED" : "FLAG_DISABLED");
                })
                .orElse(new FlagEvaluateResponse(key, false, "FLAG_NOT_FOUND"));
    }

    @Override
    public Flag create(Flag flag) {
        flag.setCreatedAt(Instant.now());
        flag.setUpdatedAt(Instant.now());
        return flagRepository.save(flag);
    }

    @Override
    public Flag getById(UUID id) {
        return flagRepository.findById(id).orElseThrow(() -> new FlagNotFoundException(id.toString()));
    }

    @Override
    public Flag update(UUID id, String description) {
        Flag flag = flagRepository.findById(id).orElseThrow(() -> new FlagNotFoundException(id.toString()));
        flag.setDescription(description);
        flag.setUpdatedAt(Instant.now());
        cacheService.delete(CacheKeyUtil.forFlag(flag.getKey()));
        return flagRepository.save(flag);
    }

    @Override
    public Flag enable(UUID id) {
        Flag flag = flagRepository.findById(id).orElseThrow(() -> new FlagNotFoundException(id.toString()));
        flag.setEnabled(true);
        flag.setUpdatedAt(Instant.now());
        cacheService.delete(CacheKeyUtil.forFlag(flag.getKey()));
        return flagRepository.save(flag);
    }

    @Override
    public Flag disable(UUID id) {
        Flag flag = flagRepository.findById(id).orElseThrow(() -> new FlagNotFoundException(id.toString()));
        flag.setEnabled(false);
        flag.setUpdatedAt(Instant.now());
        cacheService.delete(CacheKeyUtil.forFlag(flag.getKey()));
        return flagRepository.save(flag);
    }

    @Override
    public void delete(UUID id) {
        Flag flag = flagRepository.findById(id).orElseThrow(() -> new FlagNotFoundException(id.toString()));
        cacheService.delete(CacheKeyUtil.forFlag(flag.getKey()));
        flagRepository.deleteById(id);
    }

    @Override
    public Page<Flag> findAll(Pageable pageable) {
        return flagRepository.findAll(pageable);
    }

    @Override
    public Page<Flag> findAllByOrganizationId(UUID organizationId, Pageable pageable) {
        return flagRepository.findAllByOrganizationId(organizationId, pageable);
    }

    @Override
    public Page<Flag> findAllByProjectId(UUID projectId, Pageable pageable) {
        return flagRepository.findAllByProjectId(projectId, pageable);
    }
}
