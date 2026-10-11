package com.jobapply.jobapplybackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "jobs",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_job_source_external_id",
                columnNames = {"source", "external_job_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_job_id")
    private String externalJobId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobSource source;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    private String location;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String salaryCurrency;

    private Integer minExperience;

    private Integer maxExperience;

    private String jobUrl;

    private LocalDateTime postedAt;

    @Column(nullable = false)
    private LocalDateTime discoveredAt;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}