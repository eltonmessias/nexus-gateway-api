package com.nexus.nexusiam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_members", schema = "nexus_iam",
        uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "org_member_id"}))
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class TeamMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "org_member_id", nullable = false)
    private OrgMemberEntity orgMember;

    @Column(nullable = false)
    private Instant joinedAt;
}
