package com.jobapply.jobapplybackend.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private String location;

    private String linkedinUrl;

    private String githubUrl;

    private String summary;
}