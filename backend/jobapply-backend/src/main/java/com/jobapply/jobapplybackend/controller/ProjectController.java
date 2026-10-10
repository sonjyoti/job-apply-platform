package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.ProjectRequest;
import com.jobapply.jobapplybackend.dto.ProjectResponse;
import com.jobapply.jobapplybackend.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> addProject(
            @PathVariable Long profileId,
            @Valid @RequestBody ProjectRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.addProject(profileId, request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @PathVariable Long profileId) {

        return ResponseEntity.ok(
                projectService.getProjects(profileId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long profileId,
            @PathVariable Long projectId) {

        projectService.deleteProject(profileId, projectId);

        return ResponseEntity.noContent().build();
    }
}
