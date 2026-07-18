package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.port.in.OrganizationUseCase;
import com.nexus.nexusiam.infrastructure.mapper.OrganizationPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.OrganizationRequest;
import com.nexus.nexusiam.presentation.dto.response.OrganizationResponse;
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
@RequestMapping("/api/iam/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Organization management")
public class OrganizationController {

    private final OrganizationUseCase organizationUseCase;
    private final OrganizationPresentationMapper mapper;

    @Operation(summary = "Create organization")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Organization created"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Slug already exists")
    })
    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(organizationUseCase.create(mapper.toDomain(request))));
    }

    @Operation(summary = "List organizations (paginated)")
    @ApiResponse(responseCode = "200", description = "Paginated list of organizations")
    @GetMapping
    public ResponseEntity<PagedResult<OrganizationResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResult<OrganizationResponse> result = organizationUseCase.findAll(page, size)
                .map(mapper::toResponse);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get organization by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization found"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(organizationUseCase.findById(id)));
    }

    @Operation(summary = "Update organization")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Organization updated"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(@PathVariable UUID id, @Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.ok(mapper.toResponse(organizationUseCase.update(id, mapper.toDomain(request))));
    }

    @Operation(summary = "Delete organization")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Organization deleted"),
        @ApiResponse(responseCode = "404", description = "Organization not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        organizationUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
