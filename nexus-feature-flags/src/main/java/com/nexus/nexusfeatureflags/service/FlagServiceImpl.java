package com.nexus.nexusfeatureflags.service;

import com.nexus.nexuscachelayer.service.CacheService;
import com.nexus.nexuscachelayer.util.CacheKeyUtil;
import com.nexus.nexuscommons.dto.response.FlagEvaluateResponse;
import com.nexus.nexuscommons.exception.FlagNotFoundException;
import com.nexus.nexusfeatureflags.model.Flag;
import com.nexus.nexusfeatureflags.repository.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public FlagEvaluateResponse evaluate(String flagKey, String environment) {

        Optional<Object> cachedFlag = cacheService.get(CacheKeyUtil.forFlag(flagKey));
        if (cachedFlag.isPresent()) {
            Flag cached = (Flag) cachedFlag.get();
            return new FlagEvaluateResponse(cached.getFlagKey(), cached.getValue(), "Evaluated");
        }
        Flag flag = flagRepository.findByFlagKeyAndEnvironment(flagKey, environment).orElseThrow(() -> new FlagNotFoundException(flagKey));

        cacheService.set(CacheKeyUtil.forFlag(flagKey), flag);
        return new FlagEvaluateResponse(
                flagKey,
                flag.getValue(),
                "Evaluated"
        );
    }

    @Override
    public Flag create(Flag flag) {
        return flagRepository.save(flag);
    }

    @Override
    public Flag update(UUID id, Flag flag) {
        Flag oldFlag = flagRepository.findById(id).orElseThrow(() -> new FlagNotFoundException("Flag not found"));
        oldFlag.setFlagKey(flag.getFlagKey());
        oldFlag.setEnvironment(flag.getEnvironment());
        oldFlag.setEnabled(flag.isEnabled());
        oldFlag.setValue(flag.getValue());
        flagRepository.save(oldFlag);
        return oldFlag;
    }

    @Override
    public void delete(UUID id) {
        flagRepository.deleteById(id);
    }

    @Override
    public List<Flag> findAll() {
        return flagRepository.findAll();
    }
}
