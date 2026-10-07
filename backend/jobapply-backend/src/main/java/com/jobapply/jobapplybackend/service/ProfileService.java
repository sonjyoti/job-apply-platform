package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.ProfileRequest;
import com.jobapply.jobapplybackend.dto.ProfileResponse;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileResponse createProfile(ProfileRequest request) {

        Profile profile = Profile.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .location(request.getLocation())
                .linkedinUrl(request.getLinkedinUrl())
                .githubUrl(request.getGithubUrl())
                .summary(request.getSummary())
                .build();

        Profile savedProfile = profileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    public ProfileResponse getProfile(Long id) {

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return mapToResponse(profile);
    }

    private ProfileResponse mapToResponse(Profile profile) {

        return ProfileResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .email(profile.getEmail())
                .phone(profile.getPhone())
                .location(profile.getLocation())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .summary(profile.getSummary())
                .build();
    }
}