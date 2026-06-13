package com.nexus.nexusiam.presentation.controller;


import com.nexus.nexusiam.domain.port.in.OrganizationUseCase;
import com.nexus.nexusiam.infrastructure.mapper.OrganizationPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.OrganizationRequest;
import com.nexus.nexusiam.presentation.dto.response.OrganizationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/iam/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationUseCase organizationUseCase;
    private final OrganizationPresentationMapper mapper;

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(organizationUseCase.create(mapper.toDomain(request))));
    }

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> findAll(){
        return ResponseEntity.ok(organizationUseCase.findAll().stream().map(mapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(mapper.toResponse(organizationUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(@PathVariable UUID id, @Valid @RequestBody OrganizationRequest request){
        return ResponseEntity.ok(mapper.toResponse(organizationUseCase.update(id, mapper.toDomain(request))));
    }

    @DeleteMapping("/{id]")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        organizationUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
