package com.nexus.nexusgateway.controller;

import com.nexus.nexuscommons.dto.request.JobRequest;
import com.nexus.nexuscommons.dto.response.JobResponse;
import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusjobqueue.service.JobService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final AuthorizationPort authorization;

    @Autowired
    public JobController(JobService jobService, AuthorizationPort authorization) {
        this.jobService = jobService;
        this.authorization = authorization;
    }

    @PostMapping
    public ResponseEntity<JobResponse> submitJob(@Valid @RequestBody JobRequest request) {
        if (request.organizationId() != null) {
            authorization.requireSameOrg(request.organizationId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.submit(request));
    }

    @GetMapping
    public ResponseEntity<PagedResult<JobResponse>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) String status) {
        UUID scopedOrg = authorization.resolveOrgScope(organizationId);
        if (scopedOrg != null) {
            // A tenant only ever sees their own organisation's jobs.
            return ResponseEntity.ok(jobService.getByOrganizationId(scopedOrg, page, size));
        }
        // Platform admin, unfiltered — may narrow by status.
        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(jobService.getByStatus(status, page, size));
        }
        return ResponseEntity.ok(jobService.getAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(@PathVariable UUID id) {
        JobResponse job = jobService.getById(id);
        authorization.requireSameOrg(job.organizationId());
        return ResponseEntity.ok(job);
    }

    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Long>> getJobStats() {
        authorization.requirePlatformAdmin();
        return ResponseEntity.ok(jobService.getStatusCounts());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelJob(@PathVariable UUID id) {
        authorization.requireOrgContributor(jobService.getById(id).organizationId());
        jobService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<JobResponse> retryJob(@PathVariable UUID id) {
        authorization.requireOrgContributor(jobService.getById(id).organizationId());
        return ResponseEntity.ok(jobService.retry(id));
    }
}
