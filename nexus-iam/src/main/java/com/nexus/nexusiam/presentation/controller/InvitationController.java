package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.application.usecase.InvitationUseCaseImpl;
import com.nexus.nexusiam.presentation.dto.request.AcceptInvitationRequest;
import com.nexus.nexusiam.presentation.dto.response.AuthResponse;
import com.nexus.nexusiam.presentation.dto.response.InvitationDetailsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/iam/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "Accept an organisation invitation (public)")
public class InvitationController {

    private final InvitationUseCaseImpl invitationUseCase;

    @Operation(summary = "Get invitation details by token")
    @GetMapping("/{token}")
    public ResponseEntity<InvitationDetailsResponse> get(@PathVariable String token) {
        return ResponseEntity.ok(invitationUseCase.getByToken(token));
    }

    @Operation(summary = "Accept an invitation — set a password and activate the account")
    @PostMapping("/{token}/accept")
    public ResponseEntity<AuthResponse> accept(@PathVariable String token,
                                               @Valid @RequestBody AcceptInvitationRequest request) {
        return ResponseEntity.ok(invitationUseCase.accept(token, request.password()));
    }
}
