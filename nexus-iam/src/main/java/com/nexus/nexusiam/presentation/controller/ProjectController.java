package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.domain.port.in.ProjectUseCase;
import com.nexus.nexusiam.infrastructure.mapper.ProjectPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.ProjectRequest;
import com.nexus.nexusiam.presentation.dto.response.ProjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/iam/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectUseCase projectUseCase;
    private final ProjectPresentationMapper mapper;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(projectUseCase.create(mapper.toDomain(request))));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> findAll() {
        return ResponseEntity.ok(projectUseCase.findAll().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(projectUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(@PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(mapper.toResponse(projectUseCase.update(id, mapper.toDomain(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        projectUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
