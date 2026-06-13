package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.domain.port.in.TeamUseCase;
import com.nexus.nexusiam.infrastructure.mapper.TeamPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.TeamRequest;
import com.nexus.nexusiam.presentation.dto.response.TeamResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/iam/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamUseCase teamUseCase;
    private final TeamPresentationMapper mapper;

    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(teamUseCase.create(mapper.toDomain(request))));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> findAll() {
        return ResponseEntity.ok(teamUseCase.findAll().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(teamUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> update(@PathVariable UUID id, @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(mapper.toResponse(teamUseCase.update(id, mapper.toDomain(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teamUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
