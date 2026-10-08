package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByProfileId(Long profileId);
}
