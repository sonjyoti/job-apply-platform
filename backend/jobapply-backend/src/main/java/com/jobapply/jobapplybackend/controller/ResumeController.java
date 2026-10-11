package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.ResumeResponse;
import com.jobapply.jobapplybackend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> uploadResume(
            @PathVariable Long profileId,
            @RequestParam("file") MultipartFile file)
            throws IOException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.uploadResume(profileId, file));
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> getResumes(
            @PathVariable Long profileId) {

        return ResponseEntity.ok(resumeService.getResumes(profileId));
    }

    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable Long profileId,
            @PathVariable Long resumeId) throws IOException {

        resumeService.deleteResume(profileId, resumeId);

        return ResponseEntity.noContent().build();
    }
}