package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.Proficiency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileSkillRequest {

    @NotBlank
    private String skillName;

    @NotNull
    private Proficiency proficiency;
}
