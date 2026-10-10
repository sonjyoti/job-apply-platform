package com.jobapply.jobapplybackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preferred_locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreferredLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String locationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preference_id", nullable = false)
    private JobPreference preference;
}
