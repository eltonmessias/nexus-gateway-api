package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.infrastructure.persistence.entity.AuditLogEntity;
import com.nexus.nexusiam.infrastructure.persistence.repository.AuditLogJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iam/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Immutable audit trail")
public class AuditLogController {

    private final AuditLogJpaRepository auditLogJpaRepository;

    @Operation(summary = "Get audit logs by organization")
    @ApiResponse(responseCode = "200", description = "Paginated audit log")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<Page<AuditLogEntity>> getByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(
                        organizationId, PageRequest.of(page, size)));
    }

    @Operation(summary = "Get audit logs by actor")
    @ApiResponse(responseCode = "200", description = "Paginated audit log")
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
