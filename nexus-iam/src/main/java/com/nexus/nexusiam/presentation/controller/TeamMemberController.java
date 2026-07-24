package com.nexus.nexusiam.presentation.controller;

import com.nexus.nexusiam.domain.port.in.TeamMemberUseCase;
import com.nexus.nexusiam.infrastructure.mapper.TeamMemberPresentationMapper;
import com.nexus.nexusiam.presentation.dto.request.TeamMemberRequest;
import com.nexus.nexusiam.presentation.dto.response.TeamMemberResponse;
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
@RequestMapping("/api/iam/teams/{teamId}/members")
@RequiredArgsConstructor
@Tag(name = "Team Members", description = "Team membership management")
public class TeamMemberController {

    private final TeamMemberUseCase teamMemberUseCase;
    private final TeamMemberPresentationMapper mapper;

    @Operation(summary = "List members of a team")
    @GetMapping
    public ResponseEntity<List<TeamMemberResponse>> listMembers(@PathVariable UUID teamId) {
        return ResponseEntity.ok(
                teamMemberUseCase.listMembers(teamId).stream().map(mapper::toResponse).toList()
        );
    }

    @Operation(summary = "Add member to a team")
    @PostMapping
    public ResponseEntity<TeamMemberResponse> addMember(
            @PathVariable UUID teamId,
            @Valid @RequestBody TeamMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(teamMemberUseCase.addMember(teamId, request.orgMemberId())));
    }

    @Operation(summary = "Remove member from a team")
    @DeleteMapping("/{orgMemberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID teamId,
            @PathVariable UUID orgMemberId) {
        teamMemberUseCase.removeMember(teamId, orgMemberId);
        return ResponseEntity.noContent().build();
    }
}
