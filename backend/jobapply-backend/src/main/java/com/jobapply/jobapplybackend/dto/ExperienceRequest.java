package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExperienceRequest {

    @NotBlank
    private String company;

    @NotBlank
    private String jobTitle;

    @NotNull
    private EmploymentType employmentType;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private Boolean currentlyWorking;

    private String description;
}