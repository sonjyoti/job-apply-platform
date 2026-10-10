package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.PreferredRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferredRoleRepository extends JpaRepository<PreferredRole, Long> {

    List<PreferredRole> findByPreferenceId(Long preferenceId);
}
