
package com.jobapply.jobapplybackend.controller;

import com.jobapply.jobapplybackend.dto.JobMatchResponse;
import com.jobapply.jobapplybackend.service.JobMatchingService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/job-matches")
@RequiredArgsConstructor
public class JobMatchingController {

    private final JobMatchingService jobMatchingService;

    @GetMapping
    public ResponseEntity<List<JobMatchResponse>> getJobMatches(
            @PathVariable Long profileId,
            @RequestParam(defaultValue = "0") double minScore,
            @RequestParam(required = false)
            Integer maxMissingRequiredSkills,
            @RequestParam(defaultValue = "false")
            boolean fresherFriendlyOnly) {

        if (minScore < 0 || minScore > 100) {
            throw new IllegalArgumentException(
                    "minScore must be between 0 and 100"
            );
        }

        if (maxMissingRequiredSkills != null
                && maxMissingRequiredSkills < 0) {
            throw new IllegalArgumentException(
                    "maxMissingRequiredSkills cannot be negative"
            );
        }

        List<JobMatchResponse> matches =
                jobMatchingService.findMatches(
                        profileId,
                        minScore,
                        maxMissingRequiredSkills,
                        fresherFriendlyOnly
                );

        return ResponseEntity.ok(matches);
    }
}
