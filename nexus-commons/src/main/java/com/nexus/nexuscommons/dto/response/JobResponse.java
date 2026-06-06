package com.nexus.nexuscommons.dto.response;

import java.time.Instant;
import java.util.UUID;

public record JobResponse(
        UUID jobId,
        String status,
        Instant createdAt
) {
}
