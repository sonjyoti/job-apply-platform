package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.ExperienceRequest;
import com.jobapply.jobapplybackend.dto.ExperienceResponse;
import com.jobapply.jobapplybackend.entity.Experience;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.repository.ExperienceRepository;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final ProfileRepository profileRepository;

    public ExperienceResponse addExperience(
            Long profileId,
            ExperienceRequest request) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        Experience experience = Experience.builder()
                .company(request.getCompany())
                .jobTitle(request.getJobTitle())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .currentlyWorking(request.getCurrentlyWorking())
                .description(request.getDescription())
                .profile(profile)
                .build();

        Experience saved = experienceRepository.save(experience);

        return mapToResponse(saved);
    }

    public List<ExperienceResponse> getExperiences(Long profileId) {

        if (!profileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return experienceRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteExperience(
            Long profileId,
            Long experienceId) {

        Experience experience = experienceRepository
                .findById(experienceId)
                .orElseThrow(() ->
                        new RuntimeException("Experience not found"));

        if (!experience.getProfile().getId().equals(profileId)) {
            throw new RuntimeException(
                    "Experience does not belong to this profile");
        }

        experienceRepository.delete(experience);
    }

    private ExperienceResponse mapToResponse(
            Experience experience) {

        return ExperienceResponse.builder()
                .id(experience.getId())
                .company(experience.getCompany())
                .jobTitle(experience.getJobTitle())
                .employmentType(experience.getEmploymentType())
                .startDate(experience.getStartDate())
                .endDate(experience.getEndDate())
                .currentlyWorking(experience.getCurrentlyWorking())
                .description(experience.getDescription())
                .build();
    }
}