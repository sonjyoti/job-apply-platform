package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.JobPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPreferenceRepository extends JpaRepository<JobPreference, Long> {

    Optional<JobPreference> findByProfileId(Long profileId);
}