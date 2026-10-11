package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.ResumeResponse;
import com.jobapply.jobapplybackend.entity.Profile;
import com.jobapply.jobapplybackend.entity.Resume;
import com.jobapply.jobapplybackend.repository.ProfileRepository;
import com.jobapply.jobapplybackend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ProfileRepository profileRepository;

    @Value("${app.upload.resume-dir}")
    private String uploadDirectory;

    public ResumeResponse uploadResume(
            Long profileId,
            MultipartFile file) throws IOException {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file");
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Resume must not exceed 5 MB");
        }

        String originalName = StringUtils.cleanPath(
                Objects.requireNonNull(file.getOriginalFilename()));

        if (!originalName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            throw new IllegalArgumentException(
                    "Only PDF resumes are supported");
        }

        // Basic signature check: a PDF normally starts with %PDF-
        byte[] header = file.getInputStream().readNBytes(5);

        if (!Arrays.equals(header, "%PDF-".getBytes(StandardCharsets.US_ASCII))) {
            throw new IllegalArgumentException("Invalid PDF file");
        }

        Path directory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(directory);

        // Generate our own filename rather than trusting the upload name.
        String storedName = UUID.randomUUID() + ".pdf";
        Path destination = directory.resolve(storedName).normalize();

        if (!destination.startsWith(directory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(file.getInputStream(), destination);

        Resume resume = Resume.builder()
                .fileName(originalName)
                .fileUrl(directory.relativize(destination).toString())
                .fileType("application/pdf")
                .fileSize(file.getSize())
                .uploadedAt(LocalDateTime.now())
                .profile(profile)
                .build();

        return mapToResponse(resumeRepository.save(resume));
    }

    public List<ResumeResponse> getResumes(Long profileId) {

        if (!profileRepository.existsById(profileId)) {
            throw new RuntimeException("Profile not found");
        }

        return resumeRepository.findByProfileId(profileId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteResume(Long profileId, Long resumeId)
            throws IOException {

        Resume resume = resumeRepository
                .findByIdAndProfileId(resumeId, profileId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        Path directory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Path filePath = directory.resolve(resume.getFileUrl())
                .normalize();

        if (!filePath.startsWith(directory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.deleteIfExists(filePath);
        resumeRepository.delete(resume);
    }

    private ResumeResponse mapToResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .fileName(resume.getFileName())
                .fileType(resume.getFileType())
                .fileSize(resume.getFileSize())
                .uploadedAt(resume.getUploadedAt())
                .build();
    }
}