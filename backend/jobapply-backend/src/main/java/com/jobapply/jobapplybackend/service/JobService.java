package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.JobRequest;
import com.jobapply.jobapplybackend.dto.JobResponse;
import com.jobapply.jobapplybackend.entity.Job;
import com.jobapply.jobapplybackend.repository.JobRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Transactional
    public JobResponse createJob(JobRequest request) {

        // Prevent duplicate imports when the source provides an ID.
        if (request.getExternalJobId() != null
                && !request.getExternalJobId().isBlank()
                && jobRepository.existsBySourceAndExternalJobId(
                request.getSource(),
                request.getExternalJobId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This job has already been imported"
            );
        }

        // Validate salary range.
        if (request.getSalaryMin() != null
                && request.getSalaryMax() != null
                && request.getSalaryMin()
                .compareTo(request.getSalaryMax()) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum salary cannot exceed maximum salary"
            );
        }

        // Validate experience range.
        if (request.getMinExperience() != null
                && request.getMaxExperience() != null
                && request.getMinExperience()
                > request.getMaxExperience()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum experience cannot exceed maximum experience"
            );
        }

        Job job = new Job();

        job.setExternalJobId(request.getExternalJobId());
        job.setSource(request.getSource());
        job.setTitle(request.getTitle());
        job.setCompany(request.getCompany());
        job.setLocation(request.getLocation());
        job.setWorkMode(request.getWorkMode());
        job.setDescription(request.getDescription());

        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setSalaryCurrency(
                request.getSalaryCurrency() == null
                        || request.getSalaryCurrency().isBlank()
                        ? "INR"
                        : request.getSalaryCurrency()
        );

        job.setMinExperience(request.getMinExperience());
        job.setMaxExperience(request.getMaxExperience());

        job.setJobUrl(request.getJobUrl());
        job.setPostedAt(request.getPostedAt());
        job.setDiscoveredAt(LocalDateTime.now());

        Job savedJob = jobRepository.save(job);

        return toResponse(savedJob);
    }

    public JobResponse getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"
                ));

        return toResponse(job);
    }

    public Page<JobResponse> getJobs(String title, Pageable pageable) {

        Page<Job> jobs;

        if (title != null && !title.isBlank()) {
            jobs = jobRepository
                    .findByTitleContainingIgnoreCase(title, pageable);
        } else {
            jobs = jobRepository.findAll(pageable);
        }

        return jobs.map(this::toResponse);
    }

    private JobResponse toResponse(Job job) {

        JobResponse response = new JobResponse();

        response.setId(job.getId());
        response.setExternalJobId(job.getExternalJobId());
        response.setSource(job.getSource());

        response.setTitle(job.getTitle());
        response.setCompany(job.getCompany());
        response.setLocation(job.getLocation());
        response.setWorkMode(job.getWorkMode());
        response.setDescription(job.getDescription());

        response.setSalaryMin(job.getSalaryMin());
        response.setSalaryMax(job.getSalaryMax());
        response.setSalaryCurrency(job.getSalaryCurrency());

        response.setMinExperience(job.getMinExperience());
        response.setMaxExperience(job.getMaxExperience());

        response.setJobUrl(job.getJobUrl());
        response.setPostedAt(job.getPostedAt());
        response.setDiscoveredAt(job.getDiscoveredAt());
        response.setCreatedAt(job.getCreatedAt());

        return response;
    }
}