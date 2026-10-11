package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.Job;
import com.jobapply.jobapplybackend.entity.JobSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {

    Page<Job> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    boolean existsBySourceAndExternalJobId(
            JobSource source,
            String externalJobId
    );
}