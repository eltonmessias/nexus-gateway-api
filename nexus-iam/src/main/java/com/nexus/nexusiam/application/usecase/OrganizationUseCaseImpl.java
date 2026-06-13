package com.nexus.nexusiam.application.usecase;

import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.port.in.OrganizationUseCase;
import com.nexus.nexusiam.domain.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationUseCaseImpl implements OrganizationUseCase {
    private final OrganizationService organizationService;

    @Override
    public Organization create(Organization organization) {
        return organizationService.create(organization);
    }

    @Override
    public Organization update(UUID id, Organization organization) {
        return organizationService.update(id, organization);
    }

    @Override
    public Organization findById(UUID id) {
        return organizationService.findById(id);
    }

    @Override
    public List<Organization> findAll() {
        return organizationService.findAll();
    }

    @Override
    public void delete(UUID id) {
        organizationService.delete(id);
    }
}
