package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.ExperienceRequest;
import com.jobapply.jobapplybackend.dto.ExperienceResponse;
import com.jobapply.jobapplybackend.service.ExperienceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    @PostMapping
    public ResponseEntity<ExperienceResponse> addExperience(
            @PathVariable Long profileId,
            @Valid @RequestBody ExperienceRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(experienceService.addExperience(
                        profileId,
                        request));
    }

    @GetMapping
    public ResponseEntity<List<ExperienceResponse>> getExperiences(
            @PathVariable Long profileId) {

        return ResponseEntity.ok(
                experienceService.getExperiences(profileId));
    }

    @DeleteMapping("/{experienceId}")
    public ResponseEntity<Void> deleteExperience(
            @PathVariable Long profileId,
            @PathVariable Long experienceId) {

        experienceService.deleteExperience(
                profileId,
                experienceId);

        return ResponseEntity.noContent().build();
    }
}
