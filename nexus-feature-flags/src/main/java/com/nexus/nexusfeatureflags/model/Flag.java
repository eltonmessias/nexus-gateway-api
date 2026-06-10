package com.nexus.nexusfeatureflags.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String flagKey;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String environment;

    @Column(nullable = false)
    private boolean enabled;


}
