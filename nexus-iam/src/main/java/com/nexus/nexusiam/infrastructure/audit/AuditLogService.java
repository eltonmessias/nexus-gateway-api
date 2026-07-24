package com.nexus.nexusiam.infrastructure.audit;

import com.nexus.nexusiam.domain.model.ActorType;
import com.nexus.nexusiam.domain.model.AuditAction;
import com.nexus.nexusiam.infrastructure.persistence.entity.AuditLogEntity;
import com.nexus.nexusiam.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogJpaRepository auditLogJpaRepository;

    @Async
    public void log(AuditAction action,
                    String actorId,
                    String actorName,
                    ActorType actorType,
                    UUID organizationId,
                    String resourceType,
                    String resourceId,
                    Map<String, Object> metadata,
                    String ipAddress) {
        auditLogJpaRepository.save(AuditLogEntity.builder()
                .action(action.name())
                .actorId(actorId)
                .actorName(actorName)
                .actorType(actorType)
                .organizationId(organizationId)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .metadata(metadata)
                .ipAddress(ipAddress)
                .createdAt(Instant.now())
                .build());
    }
}
