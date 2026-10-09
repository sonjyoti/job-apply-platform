package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.EmploymentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ExperienceResponse {

    private Long id;

    private String company;

    private String jobTitle;

    private EmploymentType employmentType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean currentlyWorking;

    private String description;
}
