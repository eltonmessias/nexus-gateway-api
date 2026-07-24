package com.nexus.nexusjobqueue.repository;

import com.nexus.nexusjobqueue.model.Job;
import com.nexus.nexusjobqueue.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByStatus(JobStatus status);
    org.springframework.data.domain.Page<Job> findAllByStatus(JobStatus status, org.springframework.data.domain.Pageable pageable);
    long countByStatus(JobStatus status);
    org.springframework.data.domain.Page<Job> findAllByOrganizationId(UUID organizationId, org.springframework.data.domain.Pageable pageable);
}
