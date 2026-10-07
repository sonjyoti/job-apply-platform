package com.jobapply.jobapplybackend.repository;

import com.jobapply.jobapplybackend.entity.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {
    List<ProfileSkill> findByProfileId(Long profileId);
    boolean existsByProfileIdAndSkillId(Long profileId, Long skillId);
}
