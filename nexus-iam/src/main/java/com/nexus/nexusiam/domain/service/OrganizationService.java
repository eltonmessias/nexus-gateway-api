package com.nexus.nexusiam.domain.service;

import com.nexus.nexusiam.domain.exception.OrganizationNotFoundException;
import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.port.in.OrganizationUseCase;
import com.nexus.nexusiam.domain.port.out.OrganizationRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public Organization create(Organization organization) {
        return organizationRepository.save(organization);
    }

    public Organization update(UUID id, Organization organization) {
        organizationRepository.findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
        return organizationRepository.save(organization);
    }

    public Organization findById(UUID id) {
        return organizationRepository.findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
    }

    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }

    public void delete(UUID id) {
        organizationRepository.findById(id).orElseThrow(() -> new OrganizationNotFoundException(id));
        organizationRepository.deleteById(id);
    }
}
