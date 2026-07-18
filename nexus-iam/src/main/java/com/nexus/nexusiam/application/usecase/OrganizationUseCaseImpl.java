package com.nexus.nexusiam.application.usecase;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Organization;
import com.nexus.nexusiam.domain.port.in.OrganizationUseCase;
import com.nexus.nexusiam.domain.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationUseCaseImpl implements OrganizationUseCase {
    private final OrganizationService organizationService;

    @Override
    public Organization create(Organization organization) {
        Instant now = Instant.now();
        return organizationService.create(Organization.builder()
                .name(organization.getName())
                .slug(organization.getSlug())
                .description(organization.getDescription())
                .createdAt(now)
                .updatedAt(now)
                .active(true)
                .build());
    }

    @Override
    public Organization update(UUID id, Organization organization) {
        return organizationService.update(id, Organization.builder()
                .id(id)
                .name(organization.getName())
                .slug(organization.getSlug())
                .description(organization.getDescription())
                .updatedAt(Instant.now())
                .active(organization.isActive())
                .build());
    }

    @Override
    public Organization findById(UUID id) {
        return organizationService.findById(id);
    }

    @Override
    public PagedResult<Organization> findAll(int page, int size) {
        return organizationService.findAll(page, size);
    }

    @Override
    public void delete(UUID id) {
        organizationService.delete(id);
    }
}
