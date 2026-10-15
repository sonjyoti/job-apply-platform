package com.jobapply.jobapplybackend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobMatchResponse {

    private Long jobId;
    private String title;
    private String company;
    private String location;
    private String jobUrl;

    // Overall match score, from 0 to 100.
    private double matchScore;

    // Individual matching components.
    private Double skillScore;
    private Double roleScore;
    private Double experienceScore;
    private Double locationScore;
    private Double salaryScore;

    // Explain why a job received its score.
    private List<String> matchedSkills = new ArrayList<>();
    private List<String> reasons = new ArrayList<>();
    private List<String> requiredSkills = new ArrayList<>();
    private List<String> preferredSkills = new ArrayList<>();
    private List<String> missingRequiredSkills = new ArrayList<>();
}
