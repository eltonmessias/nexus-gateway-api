package com.nexus.nexuscommons.dto.response;

import java.time.Instant;

public record JobResponse(
        String jobId,
        String status,
        Instant createdAt
) {
}
