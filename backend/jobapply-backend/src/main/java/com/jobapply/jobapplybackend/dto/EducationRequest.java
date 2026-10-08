package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.EducationLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationRequest {

    @NotNull
    private EducationLevel level;

    @NotBlank
    private String degree;

    @NotBlank
    private String institution;

    private String fieldOfStudy;

    @NotNull
    private Integer startYear;

    private Integer endYear;

    private Boolean isCurrentlyStudying;
}
