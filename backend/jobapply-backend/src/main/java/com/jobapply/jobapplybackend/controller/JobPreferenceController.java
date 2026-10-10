package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.JobPreferenceRequest;
import com.jobapply.jobapplybackend.dto.JobPreferenceResponse;
import com.jobapply.jobapplybackend.service.JobPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles/{profileId}/preferences")
@RequiredArgsConstructor
public class JobPreferenceController {

    private final JobPreferenceService preferenceService;

    @PutMapping
    public ResponseEntity<JobPreferenceResponse> savePreferences(
            @PathVariable Long profileId,
            @Valid @RequestBody JobPreferenceRequest request) {

        return ResponseEntity.ok(
                preferenceService.savePreferences(profileId, request));
    }

    @GetMapping
    public ResponseEntity<JobPreferenceResponse> getPreferences(
            @PathVariable Long profileId) {

        return ResponseEntity.ok(
                preferenceService.getPreferences(profileId));
    }
}
