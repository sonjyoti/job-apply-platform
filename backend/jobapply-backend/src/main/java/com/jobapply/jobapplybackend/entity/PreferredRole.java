package com.jobapply.jobapplybackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferred_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferredRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preference_id", nullable = false)
    private JobPreference preference;
}
