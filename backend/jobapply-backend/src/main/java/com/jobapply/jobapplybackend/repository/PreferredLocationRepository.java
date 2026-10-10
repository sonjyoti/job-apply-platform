package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.PreferredLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferredLocationRepository extends JpaRepository<PreferredLocation, Long> {

    List<PreferredLocation> findByPreferenceId(Long preferenceId);
}