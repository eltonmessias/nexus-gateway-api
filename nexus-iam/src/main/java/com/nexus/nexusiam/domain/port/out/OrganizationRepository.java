package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.Organization;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {
    Organization save(Organization organization);
    Optional<Organization> findById(UUID id);
    Optional<Organization> findBySlug(String name);
    List<Organization> findAll();
    void deleteById(UUID id);
}
