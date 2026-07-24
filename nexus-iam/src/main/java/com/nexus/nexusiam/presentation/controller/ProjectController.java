package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.exception.TeamNotFoundException;
import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.in.ProjectUseCase;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.domain.port.out.TeamRepository;
import com.nexus.nexusiam.infrastructure.mapper.ProjectPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.ProjectRequest;
import com.nexus.nexusiam.presentation.dto.response.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/iam/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management")
public class ProjectController {

    private final ProjectUseCase projectUseCase;
    private final ProjectPresentationMapper mapper;
    private final AuthorizationPort authorization;
    private final TeamRepository teamRepository;

    @Operation(summary = "Create project")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Project created"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        authorization.requireOrgManager(request.organizationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(projectUseCase.create(mapper.toDomain(request))));
    }

    @Operation(summary = "List projects (paginated, optionally filtered by organization)")
    @ApiResponse(responseCode = "200", description = "Paginated list of projects")
    @GetMapping
    public ResponseEntity<PagedResult<ProjectResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID organizationId,
            @RequestParam(required = false) UUID teamId) {
        if (teamId != null) {
            Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException(teamId));
            authorization.requireSameOrg(team.getOrganizationId());
            return ResponseEntity.ok(projectUseCase.findByTeamId(teamId, page, size).map(mapper::toResponse));
        }
        UUID scopedOrg = authorization.resolveOrgScope(organizationId);
        if (scopedOrg != null) {
            return ResponseEntity.ok(projectUseCase.findByOrganizationId(scopedOrg, page, size).map(mapper::toResponse));
        }
        // Only a platform admin with no filter reaches here.
        return ResponseEntity.ok(projectUseCase.findAll(page, size).map(mapper::toResponse));
    }

    @Operation(summary = "Get project by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Project found"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable UUID id) {
        var project = projectUseCase.findById(id);
        authorization.requireSameOrg(project.getOrganizationId());
        return ResponseEntity.ok(mapper.toResponse(project));
    }

    @Operation(summary = "Update project")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Project updated"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        authorization.requireOrgManager(projectUseCase.findById(id).getOrganizationId());
        return ResponseEntity.ok(mapper.toResponse(projectUseCase.update(id, mapper.toDomain(request))));
    }

    @Operation(summary = "Delete project")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Project deleted"),
        @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        authorization.requireOrgManager(projectUseCase.findById(id).getOrganizationId());
        projectUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
