package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.application.usecase.OrgMemberUseCaseImpl;
import com.nexus.nexusiam.presentation.dto.request.OrgMemberInviteRequest;
import com.nexus.nexusiam.presentation.dto.request.OrgMemberRoleRequest;
import com.nexus.nexusiam.presentation.dto.response.OrgMemberInviteResponse;
import com.nexus.nexusiam.presentation.dto.response.OrgMemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/iam/members")
@RequiredArgsConstructor
@Tag(name = "Organisation Members", description = "Manage members of an organisation")
public class OrgMemberController {

    private final OrgMemberUseCaseImpl orgMemberUseCase;

    @Operation(summary = "List members of an organisation")
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<OrgMemberResponse>> list(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(
                orgMemberUseCase.findByOrganization(organizationId)
                        .stream().map(OrgMemberResponse::from).toList()
        );
    }

    @Operation(summary = "Invite a member to an organisation")
    @PostMapping("/organization/{organizationId}/invite")
    public ResponseEntity<OrgMemberInviteResponse> invite(
            @PathVariable UUID organizationId,
            @Valid @RequestBody OrgMemberInviteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                OrgMemberInviteResponse.from(orgMemberUseCase.invite(
                        organizationId, request.name(), request.email(), request.role()))
        );
    }

    @Operation(summary = "Change a member's role")
    @PutMapping("/{memberId}/role")
    public ResponseEntity<OrgMemberResponse> changeRole(
            @PathVariable UUID memberId,
            @Valid @RequestBody OrgMemberRoleRequest request) {
        return ResponseEntity.ok(OrgMemberResponse.from(
                orgMemberUseCase.changeRole(memberId, request.role())));
    }

    @Operation(summary = "Remove a member from an organisation")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> remove(@PathVariable UUID memberId) {
        orgMemberUseCase.remove(memberId);
        return ResponseEntity.noContent().build();
    }
}
