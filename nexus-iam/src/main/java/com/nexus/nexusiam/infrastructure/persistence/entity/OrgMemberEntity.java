package com.nexus.nexusiam.infrastructure.persistence.entity;

import com.nexus.nexusiam.domain.model.OrgMemberRole;
import com.nexus.nexusiam.domain.model.OrgMemberStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "org_members", schema = "nexus_iam",
        uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "email"}))
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class OrgMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrgMemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrgMemberStatus status;

    @Column(nullable = false)
    private Instant joinedAt;

    @Column(nullable = false)
    private Instant updatedAt;
}
