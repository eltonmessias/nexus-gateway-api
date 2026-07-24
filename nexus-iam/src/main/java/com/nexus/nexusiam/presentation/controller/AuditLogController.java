package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.infrastructure.persistence.entity.AuditLogEntity;
import com.nexus.nexusiam.infrastructure.persistence.repository.AuditLogJpaRepository;
import com.nexus.nexusiam.presentation.dto.response.AuditLogResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iam/audit")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Immutable audit trail")
public class AuditLogController {

    private final AuditLogJpaRepository auditLogJpaRepository;
    private final AuthorizationPort authorization;

    @Operation(summary = "List all audit logs (platform admin only)")
    @ApiResponse(responseCode = "200", description = "Global paginated audit log")
    @GetMapping
    public ResponseEntity<PagedResult<AuditLogResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        authorization.requirePlatformAdmin();
        Page<AuditLogEntity> result = auditLogJpaRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(PagedResult.of(
                result.getContent().stream().map(AuditLogResponse::from).toList(),
                page, size, result.getTotalElements()));
    }

    @Operation(summary = "Get audit logs by organization")
    @ApiResponse(responseCode = "200", description = "Paginated audit log")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<PagedResult<AuditLogResponse>> getByOrganization(
            @PathVariable UUID organizationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        authorization.requireSameOrg(organizationId);
        Page<AuditLogEntity> result = auditLogJpaRepository.findByOrganizationIdOrderByCreatedAtDesc(
                organizationId, PageRequest.of(page, size));
        return ResponseEntity.ok(PagedResult.of(
                result.getContent().stream().map(AuditLogResponse::from).toList(),
                page, size, result.getTotalElements()));
    }

    @Operation(summary = "Get audit logs by actor (platform admin only)")
    @ApiResponse(responseCode = "200", description = "Paginated audit log")
    @GetMapping("/actor/{actorId}")
    public ResponseEntity<PagedResult<AuditLogResponse>> getByActor(
            @PathVariable String actorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        authorization.requirePlatformAdmin();
        Page<AuditLogEntity> result = auditLogJpaRepository.findByActorIdOrderByCreatedAtDesc(
                actorId, PageRequest.of(page, size));
        return ResponseEntity.ok(PagedResult.of(
                result.getContent().stream().map(AuditLogResponse::from).toList(),
                page, size, result.getTotalElements()));
    }
}
