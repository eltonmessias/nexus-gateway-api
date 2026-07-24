package com.nexus.nexusgateway.controller;

import com.nexus.nexuscommons.dto.response.FlagEvaluateResponse;
import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusfeatureflags.model.Flag;
import com.nexus.nexusfeatureflags.service.FlagService;
import com.nexus.nexusgateway.dto.FlagRequest;
import com.nexus.nexusgateway.dto.FlagResponse;
import com.nexus.nexusiam.domain.exception.ProjectNotFoundException;
import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.domain.port.out.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/flags")
public class FlagController {
    private final FlagService flagService;
    private final AuthorizationPort authorization;
    private final ProjectRepository projectRepository;

    @Autowired
    public FlagController(FlagService flagService,
                          AuthorizationPort authorization,
                          ProjectRepository projectRepository) {
        this.flagService = flagService;
        this.authorization = authorization;
        this.projectRepository = projectRepository;
    }

    @GetMapping("/{key}/{environment}")
    public ResponseEntity<FlagEvaluateResponse> evaluateFlag(
            @PathVariable String key,
            @PathVariable String environment) {
        return ResponseEntity.ok(flagService.evaluate(key));
    }

    @GetMapping
    public ResponseEntity<PagedResult<FlagResponse>> getAllFlags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID projectId) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Flag> flagPage;
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException(projectId));
            authorization.requireSameOrg(project.getOrganizationId());
            flagPage = flagService.findAllByProjectId(projectId, pageRequest);
        } else {
            UUID scopedOrg = authorization.resolveOrgScope(organizationId);
            flagPage = scopedOrg != null
                    ? flagService.findAllByOrganizationId(scopedOrg, pageRequest)
                    : flagService.findAll(pageRequest); // platform admin, unfiltered
        }
        return ResponseEntity.ok(PagedResult.of(
                flagPage.getContent().stream().map(FlagResponse::from).toList(),
                page, size, flagPage.getTotalElements()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlagResponse> getFlagById(@PathVariable UUID id) {
        Flag flag = flagService.getById(id);
        authorization.requireSameOrg(orgOfProject(flag.getProjectId()));
        return ResponseEntity.ok(FlagResponse.from(flag));
    }

    @PostMapping
    public ResponseEntity<FlagResponse> createFlag(@Valid @RequestBody FlagRequest request) {
        authorization.requireOrgContributor(orgOfProject(request.projectId()));
        Flag flag = Flag.builder()
                .key(request.key())
                .description(request.description())
                .projectId(request.projectId())
                .enabled(Boolean.TRUE.equals(request.enabled()))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(FlagResponse.from(flagService.create(flag)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlagResponse> updateFlag(@PathVariable UUID id, @RequestBody FlagRequest request) {
        authorization.requireOrgContributor(orgOfProject(flagService.getById(id).getProjectId()));
        return ResponseEntity.ok(FlagResponse.from(flagService.update(id, request.description())));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<FlagResponse> enableFlag(@PathVariable UUID id) {
        authorization.requireOrgContributor(orgOfProject(flagService.getById(id).getProjectId()));
        return ResponseEntity.ok(FlagResponse.from(flagService.enable(id)));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<FlagResponse> disableFlag(@PathVariable UUID id) {
        authorization.requireOrgContributor(orgOfProject(flagService.getById(id).getProjectId()));
        return ResponseEntity.ok(FlagResponse.from(flagService.disable(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlag(@PathVariable UUID id) {
        authorization.requireOrgContributor(orgOfProject(flagService.getById(id).getProjectId()));
        flagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Resolve the organisation that owns a project, for tenant-scoped flag access. */
    private UUID orgOfProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        return project.getOrganizationId();
    }
}
