package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.ExperienceLevel;
import com.jobapply.jobapplybackend.entity.WorkMode;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class JobPreferenceRequest {

    @NotNull
    @Min(0)
    private Integer minExperience;

    @NotNull
    @Min(0)
    private Integer maxExperience;

    @DecimalMin("0.0")
    private BigDecimal minimumSalary;

    private String salaryCurrency = "INR";

    @NotEmpty
    private Set<WorkMode> workModes;

    private Set<ExperienceLevel> experienceLevels;

    @NotEmpty
    private List<@NotBlank String> preferredRoles;

    @NotEmpty
    private List<@NotBlank String> preferredLocations;
}
