package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.EducationLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationResponse {

    private Long id;

    private EducationLevel level;

    private String degree;

    private String institution;

    private String fieldOfStudy;

    private Integer startYear;

    private Integer endYear;

    private Boolean isCurrentlyStudying;
}
