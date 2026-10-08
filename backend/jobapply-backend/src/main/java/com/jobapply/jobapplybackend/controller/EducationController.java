package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.EducationRequest;
import com.jobapply.jobapplybackend.dto.EducationResponse;
import com.jobapply.jobapplybackend.service.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @PostMapping
    public ResponseEntity<EducationResponse> addEducation(
            @PathVariable Long profileId,
            @Valid @RequestBody EducationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(educationService.addEducation(profileId, request));
    }

    @GetMapping
    public ResponseEntity<List<EducationResponse>> getEducation(
            @PathVariable Long profileId) {

        return ResponseEntity.ok(
                educationService.getEducation(profileId));
    }

    @DeleteMapping("/{educationId}")
    public ResponseEntity<Void> deleteEducation(
            @PathVariable Long profileId,
            @PathVariable Long educationId) {

        educationService.deleteEducation(profileId, educationId);

        return ResponseEntity.noContent().build();
    }
}
