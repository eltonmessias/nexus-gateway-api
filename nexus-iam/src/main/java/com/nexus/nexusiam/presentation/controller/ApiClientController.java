package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.application.usecase.ApiClientUseCaseImpl;
import com.nexus.nexusiam.application.usecase.RegisterApiClientUseCaseImpl;
import com.nexus.nexusiam.domain.exception.ProjectNotFoundException;
import com.nexus.nexusiam.domain.model.ApiClient;
import com.nexus.nexusiam.domain.model.Project;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.domain.port.out.ProjectRepository;
import com.nexus.nexusiam.infrastructure.mapper.ApiClientPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.ApiClientRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientCreatedResponse;
import com.nexus.nexusiam.presentation.dto.response.ApiClientResponse;
import com.nexus.nexusiam.presentation.dto.response.ApiKeyRotatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/iam/clients")
@RequiredArgsConstructor
@Tag(name = "API Clients", description = "API client (M2M) management")
public class ApiClientController {

    private final RegisterApiClientUseCaseImpl registerApiClientUseCase;
    private final ApiClientUseCaseImpl apiClientUseCase;
    private final ApiClientPresentationMapper mapper;
    private final AuthorizationPort authorization;
    private final ProjectRepository projectRepository;

    @Operation(summary = "List API clients (paginated). Non-admins are scoped to their own organisation.")
    @ApiResponse(responseCode = "200", description = "Paginated list of clients")
    @GetMapping
    public ResponseEntity<PagedResult<ApiClientResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UUID organizationId) {
        UUID scopedOrg = authorization.resolveOrgScope(organizationId);
        if (scopedOrg != null) {
            List<ApiClient> orgClients = apiClientUseCase.findAllByOrganizationId(scopedOrg);
            int from = Math.min(page * size, orgClients.size());
            int to   = Math.min(from + size, orgClients.size());
            List<ApiClientResponse> content = orgClients.subList(from, to).stream().map(mapper::toResponse).toList();
            return ResponseEntity.ok(PagedResult.of(content, page, size, orgClients.size()));
        }
        // Platform admin, unfiltered.
        PagedResult<ApiClient> raw = apiClientUseCase.findAll(page, size);
        return ResponseEntity.ok(PagedResult.of(
                raw.content().stream().map(mapper::toResponse).toList(),
                page, size, raw.totalElements()
        ));
    }

    @Operation(summary = "Register API client", description = "Creates a new API client. The apiKey is shown only once.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Client created — store the apiKey securely"),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ApiClientCreatedResponse> register(@Valid @RequestBody ApiClientRequest request) {
        authorization.requireOrgManager(request.organizationId());
        return ResponseEntity.status(HttpStatus.CREATED).body(registerApiClientUseCase.execute(request));
    }

    @Operation(summary = "Get API client by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client found"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiClientResponse> findById(@PathVariable UUID id) {
        ApiClient client = apiClientUseCase.findById(id);
        authorization.requireSameOrg(client.getOrganizationId());
        return ResponseEntity.ok(mapper.toResponse(client));
    }

    @Operation(summary = "List API clients by organization")
    @ApiResponse(responseCode = "200", description = "List of clients")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<ApiClientResponse>> findByOrganization(@PathVariable UUID organizationId) {
        authorization.requireSameOrg(organizationId);
        return ResponseEntity.ok(apiClientUseCase.findAllByOrganizationId(organizationId)
                .stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "List API clients by project")
    @ApiResponse(responseCode = "200", description = "List of clients")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ApiClientResponse>> findByProject(@PathVariable UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));
        authorization.requireSameOrg(project.getOrganizationId());
        return ResponseEntity.ok(apiClientUseCase.findAllByProjectId(projectId)
                .stream().map(mapper::toResponse).toList());
    }

    @Operation(summary = "Delete API client")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Client deleted"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        authorization.requireOrgManager(apiClientUseCase.findById(id).getOrganizationId());
        apiClientUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deactivate API client")
    @ApiResponse(responseCode = "204", description = "Client deactivated")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        authorization.requireOrgManager(apiClientUseCase.findById(id).getOrganizationId());
        apiClientUseCase.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rotate API key", description = "Generates a new apiKey for this client. Old key is immediately invalidated. New key is shown only once.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "New API key generated"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PostMapping("/{id}/rotate-key")
    public ResponseEntity<ApiKeyRotatedResponse> rotateApiKey(@PathVariable UUID id) {
        authorization.requireOrgManager(apiClientUseCase.findById(id).getOrganizationId());
        String newApiKey = apiClientUseCase.rotateApiKey(id);
        return ResponseEntity.ok(new ApiKeyRotatedResponse(id, newApiKey, "API key rotated successfully. Store it securely — it will not be shown again."));
    }
}
