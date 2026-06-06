package com.nexus.nexusjobqueue.model;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Job {
    @Id
    private UUID id;
    private String type;
    private Map<String, Object> payload;
    private int priority;
    private JobStatus status;
    private Instant createdAt;

    public Job() {}
    public Job(String type, Map<String, Object> payload, int priority) {
        this.type = type;
        this.payload = payload;
        this.priority = priority;
        this.createdAt = Instant.now();
        this.status = JobStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
