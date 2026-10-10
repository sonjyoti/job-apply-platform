package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.JobPreferenceRequest;
import com.jobapply.jobapplybackend.dto.JobPreferenceResponse;

import com.jobapply.jobapplybackend.entity.JobPreference;
import com.jobapply.jobapplybackend.entity.PreferredLocation;
import com.jobapply.jobapplybackend.entity.PreferredRole;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.repository.JobPreferenceRepository;
import com.jobapply.jobapplybackend.repository.PreferredLocationRepository;
import com.jobapply.jobapplybackend.repository.PreferredRoleRepository;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPreferenceService {

    private final ProfileRepository profileRepository;
    private final JobPreferenceRepository preferenceRepository;
    private final PreferredRoleRepository roleRepository;
    private final PreferredLocationRepository locationRepository;

    @Transactional
    public JobPreferenceResponse savePreferences(
            Long profileId,
            JobPreferenceRequest request) {

        if (request.getMaxExperience() < request.getMinExperience()) {
            throw new IllegalArgumentException(
                    "Maximum experience cannot be less than minimum experience");
        }

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        JobPreference preference = preferenceRepository
                .findByProfileId(profileId)
                .orElseGet(() -> JobPreference.builder()
                        .profile(profile)
                        .build());

        preference.setMinExperience(request.getMinExperience());
        preference.setMaxExperience(request.getMaxExperience());
        preference.setMinimumSalary(request.getMinimumSalary());
        preference.setSalaryCurrency(
                request.getSalaryCurrency() == null
                        ? "INR"
                        : request.getSalaryCurrency().trim().toUpperCase());

        preference.setWorkModes(request.getWorkModes());
        preference.setExperienceLevels(
                request.getExperienceLevels() == null
                        ? java.util.Set.of()
                        : request.getExperienceLevels());

        preference = preferenceRepository.save(preference);
        final JobPreference savedPreference = preference;

        // Replace preferred roles
        roleRepository.deleteAll(
                roleRepository.findByPreferenceId(preference.getId()));

        List<String> roles = request.getPreferredRoles().stream()
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .distinct()
                .toList();

        List<PreferredRole> preferredRoles = roles.stream()
                .map(role -> PreferredRole.builder()
                        .roleName(role)
                        .preference(savedPreference)
                        .build())
                .toList();

        roleRepository.saveAll(preferredRoles);

        // Replace preferred locations
        locationRepository.deleteAll(
                locationRepository.findByPreferenceId(preference.getId()));

        List<String> locations = request.getPreferredLocations().stream()
                .map(String::trim)
                .filter(location -> !location.isEmpty())
                .distinct()
                .toList();

        List<PreferredLocation> preferredLocations = locations.stream()
                .map(location -> PreferredLocation.builder()
                        .locationName(location)
                        .preference(savedPreference)
                        .build())
                .toList();

        locationRepository.saveAll(preferredLocations);

        return getPreferences(profileId);
    }

    @Transactional(readOnly = true)
    public JobPreferenceResponse getPreferences(Long profileId) {

        JobPreference preference = preferenceRepository
                .findByProfileId(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Job preferences not found"));

        List<String> roles = roleRepository
                .findByPreferenceId(preference.getId())
                .stream()
                .map(PreferredRole::getRoleName)
                .toList();

        List<String> locations = locationRepository
                .findByPreferenceId(preference.getId())
                .stream()
                .map(PreferredLocation::getLocationName)
                .toList();

        return JobPreferenceResponse.builder()
                .id(preference.getId())
                .minExperience(preference.getMinExperience())
                .maxExperience(preference.getMaxExperience())
                .minimumSalary(preference.getMinimumSalary())
                .salaryCurrency(preference.getSalaryCurrency())
                .workModes(preference.getWorkModes())
                .experienceLevels(preference.getExperienceLevels())
                .preferredRoles(roles)
                .preferredLocations(locations)
                .build();
    }
}
