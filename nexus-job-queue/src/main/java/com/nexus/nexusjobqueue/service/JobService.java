package com.nexus.nexusjobqueue.service;

import com.nexus.nexuscommons.dto.request.JobRequest;
import com.nexus.nexuscommons.dto.response.JobResponse;
import com.nexus.nexuscommons.dto.response.PagedResult;

import java.util.UUID;

public interface JobService {
    JobResponse submit(JobRequest request);
    JobResponse getById(UUID id);
    PagedResult<JobResponse> getAll(int page, int size);
    PagedResult<JobResponse> getByOrganizationId(UUID organizationId, int page, int size);
    void cancel(UUID id);
    JobResponse retry(UUID id);
    PagedResult<JobResponse> getByStatus(String status, int page, int size);
    java.util.Map<String, Long> getStatusCounts();
}
