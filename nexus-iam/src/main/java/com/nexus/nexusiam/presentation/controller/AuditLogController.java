package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.infrastructure.audit.AuditLogService;
import com.nexus.nexusiam.infrastructure.persistence.entity.AuditLogEntity;
import com.nexus.nexusiam.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iam/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogJpaRepository auditLogJpaRepository;

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<Page<AuditLogEntity>> getByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(
                        organizationId, PageRequest.of(page, size)));
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<Page<AuditLogEntity>> getByActor(
            @PathVariable String actorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogJpaRepository.findByActorIdOrderByCreatedAtDesc(
                        actorId, PageRequest.of(page, size)));
    }
}
