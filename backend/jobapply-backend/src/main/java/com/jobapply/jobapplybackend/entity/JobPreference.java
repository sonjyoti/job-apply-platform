package com.jobapply.jobapplybackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "job_preferences",
        uniqueConstraints = @UniqueConstraint(
                columnNames = "profile_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer minExperience;

    private Integer maxExperience;

    @Column(precision = 12, scale = 2)
    private BigDecimal minimumSalary;

    @Column(length = 3)
    @Builder.Default
    private String salaryCurrency = "INR";

    @ElementCollection
    @CollectionTable(
            name = "preferred_work_modes",
            joinColumns = @JoinColumn(name = "preference_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "work_mode", nullable = false)
    @Builder.Default
    private Set<WorkMode> workModes = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "preferred_experience_levels",
            joinColumns = @JoinColumn(name = "preference_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    @Builder.Default
    private Set<ExperienceLevel> experienceLevels = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_id",
            nullable = false,
            unique = true
    )
    private Profile profile;
}
