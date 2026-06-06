package com.nexus.nexusjobqueue.repository;

import com.nexus.nexusjobqueue.model.Job;
import com.nexus.nexusjobqueue.model.JobStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository {
    Job save(Job job);
    Optional<Job> findById(UUID id);
    List<Job> findAll();
    List<Job> findByStatus(JobStatus status);
    void deleteById(UUID id);
}
