package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.ProfileRequest;
import com.jobapply.jobapplybackend.dto.ProfileResponse;
import com.jobapply.jobapplybackend.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(
            @Valid @RequestBody ProfileRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(profileService.createProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfile(
            @PathVariable Long id) {

        return ResponseEntity.ok(profileService.getProfile(id));
    }
}
