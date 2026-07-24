package com.nexus.nexusjobqueue.service;

import com.nexus.nexuscachelayer.service.CacheService;
import com.nexus.nexuscommons.dto.request.JobRequest;
import com.nexus.nexuscommons.dto.response.JobResponse;
import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexuscommons.event.JobCreatedEvent;
import com.nexus.nexuscommons.exception.JobNotFoundException;
import com.nexus.nexusjobqueue.kafka.JobEventProducer;
import com.nexus.nexusjobqueue.model.Job;
import com.nexus.nexusjobqueue.model.JobStatus;
import com.nexus.nexusjobqueue.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CacheService cacheService;
    private final JobEventProducer jobEventProducer;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository, CacheService cacheService, JobEventProducer jobEventProducer) {
        this.jobRepository = jobRepository;
        this.cacheService = cacheService;
        this.jobEventProducer = jobEventProducer;
    }

    @Override
    public JobResponse submit(JobRequest request) {
        Instant now = Instant.now();
        Job job = Job.builder()
                .type(request.type())
                .payload(request.payload())
                .priority(request.priority())
                .organizationId(request.organizationId())
                .status(JobStatus.PENDING)
                .retries(0)
                .maxRetries(3)
                .createdAt(now)
                .updatedAt(now)
                .build();
        jobRepository.save(job);
        jobEventProducer.publishJobCreatedEvent(new JobCreatedEvent(
                job.getId().toString(),
                job.getType(),
                job.getPriority(),
                job.getCreatedAt()
        ));
        return toResponse(job);
    }

    @Override
    public JobResponse getById(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));
        return toResponse(job);
    }

    @Override
    public PagedResult<JobResponse> getAll(int page, int size) {
        Page<Job> jobPage = jobRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PagedResult.of(
                jobPage.getContent().stream().map(this::toResponse).toList(),
                page, size, jobPage.getTotalElements()
        );
    }

    @Override
    public PagedResult<JobResponse> getByOrganizationId(UUID organizationId, int page, int size) {
        Page<Job> jobPage = jobRepository.findAllByOrganizationId(
                organizationId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PagedResult.of(
                jobPage.getContent().stream().map(this::toResponse).toList(),
                page, size, jobPage.getTotalElements()
        );
    }

    @Override
    public void cancel(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));
        job.setStatus(JobStatus.CANCELLED);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }

    @Override
    public JobResponse retry(UUID id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));
        job.setStatus(JobStatus.PENDING);
        job.setRetries(0);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
        jobEventProducer.publishJobCreatedEvent(new JobCreatedEvent(
                job.getId().toString(), job.getType(), job.getPriority(), job.getUpdatedAt()));
        return toResponse(job);
    }

    @Override
    public PagedResult<JobResponse> getByStatus(String status, int page, int size) {
        JobStatus jobStatus = JobStatus.valueOf(status.toUpperCase());
        Page<Job> jobPage = jobRepository.findAllByStatus(
                jobStatus, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return PagedResult.of(jobPage.getContent().stream().map(this::toResponse).toList(),
                page, size, jobPage.getTotalElements());
    }

    @Override
    public java.util.Map<String, Long> getStatusCounts() {
        java.util.Map<String, Long> counts = new java.util.LinkedHashMap<>();
        for (JobStatus s : JobStatus.values()) {
            counts.put(s.name(), jobRepository.countByStatus(s));
        }
        return counts;
    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getType(),
                job.getStatus().name(),
                job.getPayload(),
                job.getOrganizationId(),
                job.getRetries(),
                job.getMaxRetries(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
