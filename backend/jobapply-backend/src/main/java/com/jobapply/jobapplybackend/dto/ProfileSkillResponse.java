package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.Proficiency;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileSkillResponse {

    private Long id;

    private Long skillId;

    private String skillName;

    private Proficiency proficiency;
}
