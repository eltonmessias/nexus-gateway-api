package com.nexus.nexusiam.domain.port.in;

import com.nexus.nexusiam.domain.model.Organization;

import java.util.List;
import java.util.UUID;

public interface OrganizationUseCase {
    Organization create(Organization organization);
    Organization update(UUID id, Organization organization);
    Organization findById(UUID id);
    List<Organization> findAll();
    void delete(UUID id);
}
