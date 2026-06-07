package com.nexus.nexusjobqueue.service;

import com.nexus.nexuscachelayer.service.CacheService;
import com.nexus.nexuscommons.dto.request.JobRequest;
import com.nexus.nexuscommons.dto.response.JobResponse;
import com.nexus.nexuscommons.event.JobCreatedEvent;
import com.nexus.nexuscommons.exception.JobNotFoundException;
import com.nexus.nexusjobqueue.kafka.JobEventProducer;
import com.nexus.nexusjobqueue.model.Job;
import com.nexus.nexusjobqueue.model.JobStatus;
import com.nexus.nexusjobqueue.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        Job job = new Job(request.type(), request.payload(), request.priority());
        jobRepository.save(job);
        jobEventProducer.publishJobCreatedEvent(new JobCreatedEvent(
                job.getId().toString(),
                job.getType(),
                job.getPriority(),
                job.getCreatedAt()
        ));
        return new JobResponse(
                job.getId(),
                job.getStatus().toString(),
                job.getCreatedAt()
        );
    }

    @Override
    public JobResponse getById(UUID id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobNotFoundException("Job not found"));

        return new JobResponse(
                job.getId(),
                job.getStatus().toString(),
                job.getCreatedAt()
        );
    }

    @Override
    public List<JobResponse> getAll() {
        return jobRepository.findAll().stream()
                .map(job -> new JobResponse(job.getId(), job.getStatus().toString(), job.getCreatedAt())).toList();
    }

    @Override
    public void cancel(UUID id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new JobNotFoundException("Job not found"));
        job.setStatus(JobStatus.FAILED);
        jobRepository.save(job);
    }
}
