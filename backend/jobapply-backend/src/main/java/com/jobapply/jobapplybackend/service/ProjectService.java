package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.ProjectRequest;
import com.jobapply.jobapplybackend.dto.ProjectResponse;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.entity.Project;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import com.jobapply.jobapplybackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProfileRepository profileRepository;

    public ProjectResponse addProject(
            Long profileId,
            ProjectRequest request) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        Project project = Project.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .technologies(request.getTechnologies())
                .projectUrl(request.getProjectUrl())
                .profile(profile)
                .build();

        return mapToResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> getProjects(Long profileId) {

        if (!profileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return projectRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteProject(Long profileId, Long projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new RuntimeException("Project not found"));

        if (!project.getProfile().getId().equals(profileId)) {
            throw new RuntimeException(
                    "Project does not belong to this profile");
        }

        projectRepository.delete(project);
    }

    private ProjectResponse mapToResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .technologies(project.getTechnologies())
                .projectUrl(project.getProjectUrl())
                .build();
    }
}