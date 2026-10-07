package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.ProfileSkillRequest;
import com.jobapply.jobapplybackend.dto.ProfileSkillResponse;
import com.jobapply.jobapplybackend.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<ProfileSkillResponse> addSkill(
            @PathVariable Long profileId,
            @Valid @RequestBody ProfileSkillRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(skillService.addSkill(profileId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProfileSkillResponse>> getSkills(
            @PathVariable Long profileId) {

        return ResponseEntity.ok(
                skillService.getSkills(profileId));
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable Long profileId,
            @PathVariable Long skillId) {

        skillService.deleteSkill(profileId, skillId);

        return ResponseEntity.noContent().build();
    }
}
