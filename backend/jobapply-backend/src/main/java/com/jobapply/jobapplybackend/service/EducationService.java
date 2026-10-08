package com.jobapply.jobapplybackend.service;


import com.jobapply.jobapplybackend.dto.EducationRequest;
import com.jobapply.jobapplybackend.dto.EducationResponse;
import com.jobapply.jobapplybackend.entity.Education;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.repository.EducationRepository;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationRepository educationRepository;
    private final ProfileRepository profileRepository;

    public EducationResponse addEducation(
            Long profileId,
            EducationRequest request) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        Education education = Education.builder()
                .degree(request.getDegree())
                .educationLevel(request.getLevel())
                .institution(request.getInstitution())
                .fieldOfStudy(request.getFieldOfStudy())
                .startYear(request.getStartYear())
                .endYear(request.getEndYear())
                .isCurrentlyStudying(request.getIsCurrentlyStudying())
                .profile(profile)
                .build();

        Education saved = educationRepository.save(education);

        return mapToResponse(saved);
    }

    public List<EducationResponse> getEducation(Long profileId) {

        if (!profileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return educationRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteEducation(
            Long profileId,
            Long educationId) {

        Education education = educationRepository
                .findById(educationId)
                .orElseThrow(() ->
                        new RuntimeException("Education not found"));

        if (!education.getProfile().getId().equals(profileId)) {
            throw new RuntimeException(
                    "Education does not belong to this profile");
        }

        educationRepository.delete(education);
    }

    private EducationResponse mapToResponse(Education education) {

        return EducationResponse.builder()
                .id(education.getId())
                .level(education.getEducationLevel())
                .degree(education.getDegree())
                .institution(education.getInstitution())
                .fieldOfStudy(education.getFieldOfStudy())
                .startYear(education.getStartYear())
                .endYear(education.getEndYear())
                .isCurrentlyStudying(education.getIsCurrentlyStudying())
                .build();
    }
}
