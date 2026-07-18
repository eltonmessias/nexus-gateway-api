package com.nexus.nexusjobqueue.kafka;

import com.nexus.nexuscommons.event.JobCompletedEvent;
import com.nexus.nexuscommons.event.JobCreatedEvent;
import com.nexus.nexusjobqueue.model.Job;
import com.nexus.nexusjobqueue.model.JobStatus;
import com.nexus.nexusjobqueue.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobEventConsumer {

    private final JobRepository jobRepository;

    @KafkaListener(topics = "job.created", groupId = "${spring.kafka.consumer.group-id:nexus-job-queue}")
    @Transactional
    public void onJobCreated(JobCreatedEvent event) {
        log.debug("Received job.created event for jobId={}", event.jobId());
        jobRepository.findById(UUID.fromString(event.jobId())).ifPresent(job -> {
            job.setStatus(JobStatus.RUNNING);
            jobRepository.save(job);
            log.debug("Job {} status updated to RUNNING", event.jobId());
        });
    }

    @KafkaListener(topics = "job.completed", groupId = "${spring.kafka.consumer.group-id:nexus-job-queue}")
    @Transactional
    public void onJobCompleted(JobCompletedEvent event) {
        log.debug("Received job.completed event for jobId={} status={}", event.jobId(), event.status());
        jobRepository.findById(UUID.fromString(event.jobId())).ifPresent(job -> {
            JobStatus finalStatus = "FAILED".equalsIgnoreCase(event.status()) ? JobStatus.FAILED : JobStatus.COMPLETED;
            job.setStatus(finalStatus);
            jobRepository.save(job);
            log.debug("Job {} status updated to {}", event.jobId(), finalStatus);
        });
    }
}
