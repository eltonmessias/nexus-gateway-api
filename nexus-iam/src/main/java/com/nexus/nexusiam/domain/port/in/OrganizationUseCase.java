package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Organization;

import java.util.UUID;

public interface OrganizationUseCase {
    Organization create(Organization organization);
    Organization update(UUID id, Organization organization);
    Organization findById(UUID id);
    PagedResult<Organization> findAll(int page, int size);
    void delete(UUID id);
}
