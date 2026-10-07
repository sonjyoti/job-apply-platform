package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.ProfileSkillRequest;
import com.jobapply.jobapplybackend.dto.ProfileSkillResponse;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.entity.ProfileSkill;
import com.jobapply.jobapplybackend.entity.Skill;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import com.jobapply.jobapplybackend.repository.ProfileSkillRepository;
import com.jobapply.jobapplybackend.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final ProfileRepository profileRepository;
    private final SkillRepository skillRepository;
    private final ProfileSkillRepository profileSkillRepository;

    public ProfileSkillResponse addSkill(
            Long profileId,
            ProfileSkillRequest request) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        Skill skill = skillRepository
                .findByNameIgnoreCase(request.getSkillName().trim())
                .orElseGet(() -> {

                    Skill newSkill = Skill.builder()
                            .name(request.getSkillName().trim())
                            .build();

                    return skillRepository.save(newSkill);
                });

        if (profileSkillRepository
                .existsByProfileIdAndSkillId(profileId, skill.getId())) {

            throw new RuntimeException(
                    "Skill already added to profile");
        }

        ProfileSkill profileSkill = ProfileSkill.builder()
                .profile(profile)
                .skill(skill)
                .proficiency(request.getProficiency())
                .build();

        ProfileSkill saved = profileSkillRepository.save(profileSkill);

        return mapToResponse(saved);
    }

    public List<ProfileSkillResponse> getSkills(Long profileId) {

        if (!profileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return profileSkillRepository
                .findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteSkill(Long profileId, Long skillId) {

        ProfileSkill profileSkill =
                profileSkillRepository.findById(skillId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Profile skill not found"));

        if (!profileSkill.getProfile().getId().equals(profileId)) {
            throw new RuntimeException(
                    "Skill does not belong to this profile");
        }

        profileSkillRepository.delete(profileSkill);
    }

    private ProfileSkillResponse mapToResponse(
            ProfileSkill profileSkill) {

        return ProfileSkillResponse.builder()
                .id(profileSkill.getId())
                .skillId(profileSkill.getSkill().getId())
                .skillName(profileSkill.getSkill().getName())
                .proficiency(profileSkill.getProficiency())
                .build();
    }
}
