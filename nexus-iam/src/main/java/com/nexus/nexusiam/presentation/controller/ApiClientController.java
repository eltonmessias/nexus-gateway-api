package com.nexus.nexusiam.presentation.controller;


import com.nexus.nexusiam.application.usecase.ApiClientUseCaseImpl;
import com.nexus.nexusiam.application.usecase.RegisterApiClientUseCaseImpl;
import com.nexus.nexusiam.infrastructure.mapper.ApiClientPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.ApiClientRequest;
import com.nexus.nexusiam.presentation.dto.response.ApiClientCreatedResponse;
import com.nexus.nexusiam.presentation.dto.response.ApiClientResponse;
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
public class ApiClientController {

    private final RegisterApiClientUseCaseImpl registerApiClientUseCase;
    private final ApiClientUseCaseImpl apiClientUseCase;
    private final ApiClientPresentationMapper mapper;

    @PostMapping
    public ResponseEntity<ApiClientCreatedResponse> register(@Valid @RequestBody ApiClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerApiClientUseCase.execute(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiClientResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(apiClientUseCase.findById(id)));
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<ApiClientResponse>> findByOrganization(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(apiClientUseCase.findAllByOrganizationId(organizationId)
                .stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ApiClientResponse>> findByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(apiClientUseCase.findAllByProjectId(projectId)
                .stream().map(mapper::toResponse).toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        apiClientUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        apiClientUseCase.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
