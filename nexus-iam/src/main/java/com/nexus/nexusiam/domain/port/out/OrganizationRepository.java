package com.nexus.nexusiam.domain.port.out;

import com.nexus.nexusiam.domain.model.Organization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {
    Organization save(Organization organization);
    Optional<Organization> findById(UUID id);
    Optional<Organization> findBySlug(String slug);
    List<Organization> findAll();
    void deleteById(UUID id);
    Boolean existsById(UUID id);

    boolean existsBySlug( String slug);
}
