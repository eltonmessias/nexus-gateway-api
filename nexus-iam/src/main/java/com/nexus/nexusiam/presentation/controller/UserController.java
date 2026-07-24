package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexuscommons.dto.response.PagedResult;
import com.nexus.nexusiam.domain.port.in.UserUseCase;
import com.nexus.nexusiam.domain.port.out.AuthorizationPort;
import com.nexus.nexusiam.infrastructure.mapper.UserPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.UserRequest;
import com.nexus.nexusiam.presentation.dto.request.UserStatusRequest;
import com.nexus.nexusiam.presentation.dto.response.UserResponse;
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
@RequestMapping("/api/iam/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserUseCase userUseCase;
    private final UserPresentationMapper mapper;
    private final AuthorizationPort authorization;

    @Operation(summary = "Create user")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        authorization.requirePlatformAdmin();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(userUseCase.create(mapper.toDomain(request))));
    }

    @Operation(summary = "List users (paginated)")
    @ApiResponse(responseCode = "200", description = "Paginated list of users")
    @GetMapping
    public ResponseEntity<PagedResult<UserResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        authorization.requirePlatformAdmin();
        return ResponseEntity.ok(userUseCase.findAll(page, size).map(mapper::toResponse));
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable UUID id) {
        authorization.requirePlatformAdmin();
        return ResponseEntity.ok(mapper.toResponse(userUseCase.findById(id)));
    }

    @Operation(summary = "Update user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        authorization.requirePlatformAdmin();
        return ResponseEntity.ok(mapper.toResponse(userUseCase.update(id, mapper.toDomain(request))));
    }

    @Operation(summary = "Delete user")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        authorization.requirePlatformAdmin();
        userUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activate or deactivate a user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{id}/active")
    public ResponseEntity<UserResponse> setActive(@PathVariable UUID id, @RequestBody UserStatusRequest request) {
        authorization.requirePlatformAdmin();
        return ResponseEntity.ok(mapper.toResponse(userUseCase.setActive(id, request.active())));
    }
}
