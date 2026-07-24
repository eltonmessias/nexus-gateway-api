package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.model.Team;
import com.nexus.nexusiam.domain.port.in.TeamUseCase;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.infrastructure.mapper.TeamPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.TeamRequest;
import com.nexus.nexusiam.presentation.dto.response.TeamResponse;
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
@RequestMapping("/api/iam/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Team management")
public class TeamController {

    private final TeamUseCase teamUseCase;
    private final TeamPresentationMapper mapper;
    private final AuthorizationPort authorization;

    @Operation(summary = "Create team")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Team created"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        authorization.requireOrgManager(request.organizationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(teamUseCase.create(mapper.toDomain(request))));
    }

    @Operation(summary = "List teams (paginated). Non-admins are scoped to their own organisation.")
    @ApiResponse(responseCode = "200", description = "Paginated list of teams")
    @GetMapping
    public ResponseEntity<PagedResult<TeamResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID organizationId) {
        UUID scopedOrg = authorization.resolveOrgScope(organizationId);
        if (scopedOrg != null) {
            return ResponseEntity.ok(teamUseCase.findByOrganizationId(scopedOrg, page, size).map(mapper::toResponse));
        }
        return ResponseEntity.ok(teamUseCase.findAll(page, size).map(mapper::toResponse));
    }

    @Operation(summary = "Get team by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Team found"),
        @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable UUID id) {
        Team team = teamUseCase.findById(id);
        authorization.requireSameOrg(team.getOrganizationId());
        return ResponseEntity.ok(mapper.toResponse(team));
    }

    @Operation(summary = "Update team")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Team updated"),
        @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> update(@PathVariable UUID id, @Valid @RequestBody TeamRequest request) {
        authorization.requireOrgManager(teamUseCase.findById(id).getOrganizationId());
        return ResponseEntity.ok(mapper.toResponse(teamUseCase.update(id, mapper.toDomain(request))));
    }

    @Operation(summary = "Delete team")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Team deleted"),
        @ApiResponse(responseCode = "404", description = "Team not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        authorization.requireOrgManager(teamUseCase.findById(id).getOrganizationId());
        teamUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
