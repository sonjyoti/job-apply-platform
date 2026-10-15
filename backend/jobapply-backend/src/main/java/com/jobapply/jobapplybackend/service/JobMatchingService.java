package com.jobapply.jobapplybackend.service;

import com.jobapply.jobapplybackend.dto.JobMatchResponse;
import com.jobapply.jobapplybackend.entity.*;
import com.jobapply.jobapplybackend.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobMatchingService {

    private final ProfileRepository profileRepository;
    private final ProfileSkillRepository profileSkillRepository;
    private final JobRepository jobRepository;

    private final JobPreferenceRepository jobPreferenceRepository;
    private final PreferredRoleRepository preferredRoleRepository;
    private final PreferredLocationRepository preferredLocationRepository;

    public List<JobMatchResponse> findMatches(Long profileId) {

        // Ensure the profile exists.
        profileRepository.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profile not found"
                ));

        List<ProfileSkill> profileSkills =
                profileSkillRepository.findByProfileId(profileId);

        Set<String> skillNames = profileSkills.stream()
                .map(ps -> ps.getSkill().getName())
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        JobPreference preferences =
                jobPreferenceRepository.findByProfileId(profileId)
                        .orElse(null);

        List<String> preferredRoles = new ArrayList<>();
        List<String> preferredLocations = new ArrayList<>();

        if (preferences != null) {
            preferredRoles = preferredRoleRepository
                    .findByPreferenceId(preferences.getId())
                    .stream()
                    .map(PreferredRole::getRoleName)
                    .filter(Objects::nonNull)
                    .toList();

            preferredLocations = preferredLocationRepository
                    .findByPreferenceId(preferences.getId())
                    .stream()
                    .map(PreferredLocation::getLocationName)
                    .filter(Objects::nonNull)
                    .toList();
        }

        final JobPreference prefs = preferences;
        final List<String> roles = preferredRoles;
        final List<String> locations = preferredLocations;

        return jobRepository.findAll().stream()
                .map(job -> calculateMatch(
                        job, skillNames, prefs, roles, locations
                ))
                .sorted(Comparator.comparingDouble(
                        JobMatchResponse::getMatchScore
                ).reversed())
                .toList();
    }

    private JobMatchResponse calculateMatch(
            Job job,
            Set<String> skillNames,
            JobPreference preferences,
            List<String> preferredRoles,
            List<String> preferredLocations) {

        JobMatchResponse response = new JobMatchResponse();

        response.setJobId(job.getId());
        response.setTitle(job.getTitle());
        response.setCompany(job.getCompany());
        response.setLocation(job.getLocation());
        response.setJobUrl(job.getJobUrl());

        String searchableText = (
                nullToEmpty(job.getTitle()) + " " +
                        nullToEmpty(job.getDescription())
        ).toLowerCase();

        // 1. Skills: 40%
        SkillAnalysis analysis = analyzeSkills(
                searchableText,
                skillNames
        );

        Double skillScore = analysis.score();

        response.setSkillScore(skillScore);
        response.setMatchedSkills(analysis.matched());
        response.setRequiredSkills(analysis.required());
        response.setPreferredSkills(analysis.preferred());
        response.setMissingRequiredSkills(analysis.missingRequired());

        // 2. Role: 25%
        Double roleScore = calculateRoleScore(
                job.getTitle(), preferredRoles
        );
        response.setRoleScore(roleScore);

        // 3. Experience: 15%
        Double experienceScore = calculateExperienceScore(
                job, preferences
        );
        response.setExperienceScore(experienceScore);

        // 4. Location and work mode: 10%
        Double locationScore = calculateLocationScore(
                job, preferences, preferredLocations
        );
        response.setLocationScore(locationScore);

        // 5. Salary: 10%
        Double salaryScore = calculateSalaryScore(job, preferences);
        response.setSalaryScore(salaryScore);

        double weightedTotal = 0.0;
        double availableWeight = 0.0;

        Object[][] components = {
                {skillScore, 40.0},
                {roleScore, 25.0},
                {experienceScore, 15.0},
                {locationScore, 10.0},
                {salaryScore, 10.0}
        };

        for (Object[] component : components) {
            Double score = (Double) component[0];
            double weight = (Double) component[1];

            if (score != null) {
                weightedTotal += score * weight;
                availableWeight += weight;
            }
        }

        double overallScore = availableWeight == 0
                ? 0.0
                : weightedTotal / availableWeight;

        response.setMatchScore(
                Math.round(overallScore * 100.0) / 100.0
        );

        // Explain the result.
        if (!analysis.matched().isEmpty()) {
            response.getReasons().add(
                    "Matching skills: " +
                            String.join(", ", analysis.matched())
            );
        }

        if (!analysis.missingRequired().isEmpty()) {
            response.getReasons().add(
                    "Missing required skills: " +
                            String.join(", ", analysis.missingRequired())
            );
        }

        if (roleScore != null && roleScore >= 80) {
            response.getReasons().add(
                    "Job title aligns with preferred roles"
            );
        }

        if (locationScore != null && locationScore >= 80) {
            response.getReasons().add(
                    "Location or work mode matches your preferences"
            );
        }

        if (experienceScore == null) {
            response.getReasons().add(
                    "Experience requirements are not fully specified"
            );
        }

        if (salaryScore == null) {
            response.getReasons().add(
                    "Salary compatibility cannot be determined"
            );
        }

        return response;
    }

    private Double calculateRoleScore(
            String title,
            List<String> preferredRoles) {

        if (title == null || title.isBlank()) {
            return 0.0;
        }

        String normalizedTitle = title.toLowerCase();

        if (!preferredRoles.isEmpty()) {
            for (String role : preferredRoles) {
                if (normalizedTitle.contains(role.toLowerCase())) {
                    return 100.0;
                }
            }
            return 0.0;
        }

        // Initial defaults until the user configures preferred roles.
        String[] defaultRoles = {
                "java", "spring boot", "backend",
                "back-end", "software engineer",
                "software developer"
        };

        for (String role : defaultRoles) {
            if (normalizedTitle.contains(role)) {
                return 100.0;
            }
        }

        return 0.0;
    }

    private Double calculateExperienceScore(
            Job job, JobPreference preferences) {

        if (job.getMinExperience() == null
                && job.getMaxExperience() == null) {
            return null;
        }

        if (preferences == null
                || (preferences.getMinExperience() == null
                && preferences.getMaxExperience() == null)) {
            return null;
        }

        int jobMin = job.getMinExperience() == null
                ? 0 : job.getMinExperience();

        int jobMax = job.getMaxExperience() == null
                ? Integer.MAX_VALUE : job.getMaxExperience();

        int preferredMin = preferences.getMinExperience() == null
                ? 0 : preferences.getMinExperience();

        int preferredMax = preferences.getMaxExperience() == null
                ? Integer.MAX_VALUE : preferences.getMaxExperience();

        if (jobMax < preferredMin || jobMin > preferredMax) {
            return 0.0;
        }

        return 100.0;
    }

    private Double calculateLocationScore(
            Job job,
            JobPreference preferences,
            List<String> preferredLocations) {

        String location = nullToEmpty(job.getLocation()).toLowerCase();

        Double placeScore = null;
        if (!preferredLocations.isEmpty()) {
            placeScore = preferredLocations.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(preferred ->
                            location.contains(preferred.toLowerCase()))
                    ? 100.0 : 0.0;
        } else if (job.getWorkMode() == WorkMode.REMOTE) {
            // Remote does not guarantee worldwide eligibility.
            placeScore = location.contains("india")
                    || location.contains("worldwide")
                    || location.contains("anywhere")
                    ? 100.0 : null;
        }

        Double workModeScore = null;
        if (preferences != null
                && preferences.getWorkModes() != null
                && !preferences.getWorkModes().isEmpty()) {

            workModeScore = preferences.getWorkModes()
                    .contains(job.getWorkMode())
                    ? 100.0 : 0.0;
        }

        if (placeScore == null && workModeScore == null) {
            return null;
        }

        if (placeScore == null) {
            return workModeScore;
        }

        if (workModeScore == null) {
            return placeScore;
        }

        return (placeScore + workModeScore) / 2.0;
    }

    private Double calculateSalaryScore(
            Job job, JobPreference preferences) {

        if (preferences == null
                || preferences.getMinimumSalary() == null) {
            return null;
        }

        BigDecimal minimumSalary = preferences.getMinimumSalary();

        if (job.getSalaryMin() == null && job.getSalaryMax() == null) {
            return null;
        }

        // Never compare salary amounts in different currencies.
        String preferredCurrency = preferences.getSalaryCurrency();
        String jobCurrency = job.getSalaryCurrency();

        if (preferredCurrency != null && jobCurrency != null
                && !preferredCurrency.equalsIgnoreCase(jobCurrency)) {
            return null;
        }

        BigDecimal jobMin = job.getSalaryMin();
        BigDecimal jobMax = job.getSalaryMax();

        if (jobMax != null && jobMax.compareTo(minimumSalary) < 0) {
            return 0.0;
        }

        if (jobMin != null && jobMin.compareTo(minimumSalary) >= 0) {
            return 100.0;
        }

        // Salary range overlaps the minimum preferred salary.
        return 50.0;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record SkillAnalysis(
            Double score,
            List<String> matched,
            List<String> required,
            List<String> preferred,
            List<String> missingRequired
    ) {}

    private SkillAnalysis analyzeSkills(
            String jobText,
            Set<String> profileSkills) {

        // Initial technology vocabulary. Expand this as we add sources.
        Set<String> knownSkills = new TreeSet<>(
                Set.of(
                        "Java",
                        "Spring Boot",
                        "Spring",
                        "SQL",
                        "PostgreSQL",
                        "MySQL",
                        "REST API",
                        "Microservices",
                        "Docker",
                        "Kubernetes",
                        "Redis",
                        "Kafka",
                        "React",
                        "JavaScript",
                        "TypeScript",
                        "Node.js",
                        "Python",
                        "AWS",
                        "Git",
                        "Linux",
                        "MongoDB"
                )
        );

        // Include additional skills entered in the user's profile.
        knownSkills.addAll(profileSkills);

        List<String> required = new ArrayList<>();
        List<String> preferred = new ArrayList<>();
        List<String> mentioned = new ArrayList<>();
        List<String> matched = new ArrayList<>();
        List<String> missingRequired = new ArrayList<>();

        double totalWeight = 0.0;
        double earnedWeight = 0.0;

        for (String skill : knownSkills) {
            String normalizedSkill = skill.toLowerCase(Locale.ROOT);

            if (!containsSkill(jobText, normalizedSkill)) {
                continue;
            }

            String context = getSkillContext(jobText, normalizedSkill);

            boolean isPreferred = containsAny(
                    context,
                    "nice to have",
                    "preferred",
                    "bonus",
                    "desirable",
                    "a plus",
                    "optional"
            );

            boolean isRequired = !isPreferred && containsAny(
                    context,
                    "required",
                    "must have",
                    "essential",
                    "requirements",
                    "minimum qualifications"
            );

            double weight;

            if (isRequired) {
                required.add(skill);
                weight = 1.0;

                if (!profileSkills.contains(normalizedSkill)) {
                    missingRequired.add(skill);
                }
            } else if (isPreferred) {
                preferred.add(skill);
                weight = 0.5;
            } else {
                mentioned.add(skill);
                weight = 0.25;
            }

            totalWeight += weight;

            if (profileSkills.contains(normalizedSkill)) {
                matched.add(skill);
                earnedWeight += weight;
            }
        }

        Double score = totalWeight == 0
                ? null
                : Math.round(
                earnedWeight / totalWeight * 10000.0
        ) / 100.0;

        return new SkillAnalysis(
                score,
                matched,
                required,
                preferred,
                missingRequired
        );
    }

    private boolean containsSkill(String text, String skill) {
        String normalizedText = text.toLowerCase(Locale.ROOT);

        int index = normalizedText.indexOf(skill);

        while (index >= 0) {
            int end = index + skill.length();

            boolean validStart = index == 0
                    || !Character.isLetterOrDigit(
                    normalizedText.charAt(index - 1));

            boolean validEnd = end == normalizedText.length()
                    || !Character.isLetterOrDigit(
                    normalizedText.charAt(end));

            if (validStart && validEnd) {
                return true;
            }

            index = normalizedText.indexOf(skill, index + 1);
        }

        return false;
    }

    private String getSkillContext(String text, String skill) {
        String normalizedText = text.toLowerCase(Locale.ROOT);
        int index = normalizedText.indexOf(skill);

        if (index < 0) {
            return "";
        }

        int start = Math.max(0, index - 120);
        int end = Math.min(
                normalizedText.length(),
                index + skill.length() + 120
        );

        return normalizedText.substring(start, end);
    }

    private boolean containsAny(String text, String... phrases) {
        for (String phrase : phrases) {
            if (text.contains(phrase)) {
                return true;
            }
        }

        return false;
    }
}