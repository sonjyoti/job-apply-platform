package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.JobSource;
import com.jobapply.jobapplybackend.entity.WorkMode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobResponse {

    private Long id;
    private String externalJobId;
    private JobSource source;

    private String title;
    private String company;
    private String location;
    private WorkMode workMode;
    private String description;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String salaryCurrency;

    private Integer minExperience;
    private Integer maxExperience;

    private String jobUrl;
    private LocalDateTime postedAt;
    private LocalDateTime discoveredAt;
    private LocalDateTime createdAt;
}
