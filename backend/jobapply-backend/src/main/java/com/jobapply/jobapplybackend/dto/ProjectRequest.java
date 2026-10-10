package com.jobapply.jobapplybackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectRequest {

    @NotBlank
    private String name;

    private String description;

    private String technologies;

    private String projectUrl;
}
