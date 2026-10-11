package com.jobapply.jobapplybackend.dto;

import com.jobapply.jobapplybackend.entity.JobSource;
import com.jobapply.jobapplybackend.entity.WorkMode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class JobRequest {

    private String externalJobId;

    @NotNull
    private JobSource source;

    @NotBlank
    private String title;

    @NotBlank
    private String company;

    private String location;

    private WorkMode workMode;

    private String description;

    @DecimalMin("0.0")
    private BigDecimal salaryMin;

    @DecimalMin("0.0")
    private BigDecimal salaryMax;

    private String salaryCurrency;

    @Min(0)
    private Integer minExperience;

    @Min(0)
    private Integer maxExperience;

    private String jobUrl;

    private LocalDateTime postedAt;
}