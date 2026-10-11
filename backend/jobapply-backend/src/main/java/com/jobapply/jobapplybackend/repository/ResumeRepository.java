package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByProfileId(Long profileId);

    Optional<Resume> findByIdAndProfileId(Long id, Long profileId);
}