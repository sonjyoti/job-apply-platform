package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.ExperienceLevel;
import com.jobapply.jobapplybackend.entity.WorkMode;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Builder
public class JobPreferenceResponse {

    private Long id;
    private Integer minExperience;
    private Integer maxExperience;
    private BigDecimal minimumSalary;
    private String salaryCurrency;
    private Set<WorkMode> workModes;
    private Set<ExperienceLevel> experienceLevels;
    private List<String> preferredRoles;
    private List<String> preferredLocations;
}
