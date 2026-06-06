package com.nexus.nexusjobqueue.service;

import com.nexus.nexuscommons.dto.request.JobRequest;
import com.nexus.nexuscommons.dto.response.JobResponse;

import java.util.List;
import java.util.UUID;

public interface JobService {
    JobResponse submit(JobRequest request);
    JobResponse getById(UUID id);
    List<JobResponse> getAll();
    void cancel(UUID id);
}
