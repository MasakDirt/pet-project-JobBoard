package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CutCandidateProfileResponse;
import com.board.job.model.dto.candidate_profile.FullCandidateProfileResponse;
import com.board.job.model.mapper.candidate.CandidateProfileMapper;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/candidate-profiles")
public class CandidateProfileController {
    private final CandidateProfileMapper mapper;
    private final CandidateProfileService candidateProfileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public Set<CutCandidateProfileResponse> getAll(Authentication authentication) {
        var response = candidateProfileService.getAll()
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .collect(Collectors.toSet());
        log.info("=== GET-CANDIDATE_PROFILES === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/sorted")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getSortedVacancies(
            @RequestParam(name = "sort_by", defaultValue = "id") String[] sortBy,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sortedOrder,
            @RequestParam(defaultValue = "0") int page, Authentication authentication) {

        var sort = Sort.by(Sort.Direction.fromString(sortedOrder), sortBy);
        Pageable pageable = PageRequest.of(page, 5, sort);

        var candidateProfiles = candidateProfileService.getAllSorted(pageable)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-SORTED-CANDIDATE_PROFILES === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return candidateProfiles;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public FullCandidateProfileResponse getById(@PathVariable long id,
                                                Authentication authentication) {
        var profile = mapper.getCandidateProfileResponseFromCandidateProfile(candidateProfileService.readById(id));
        log.info("=== GET_CANDIDATE_PROFILE === {} - {}", getAuthorities(authentication), authentication.getPrincipal());

        return profile;
    }

    @PostMapping
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.principal)")
    public ResponseEntity<String> create(
            @PathVariable("owner-id") long ownerId, @RequestBody @Valid CandidateProfileRequest request,
            Authentication authentication) {

        var created = candidateProfileService.create(ownerId, mapper.getCandidateProfileFromCandidateProfileRequest(request));
        log.info("=== POST-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Candidate profile for user %s successfully created", created.getOwner().getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authCandidateProfileService.isUsersSameByIdAndUserOwnerCandidateProfile(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> update(
            @PathVariable("owner-id") long ownerId, @PathVariable long id,
            @RequestBody @Valid CandidateProfileRequest request, Authentication authentication) {

        var created = candidateProfileService.update(id, mapper.getCandidateProfileFromCandidateProfileRequest(request));
        log.info("=== PUT-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("Candidate profile for user %s successfully updated", created.getOwner().getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authCandidateProfileService.isUsersSameByIdAndUserOwnerCandidateProfile(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> delete(
            @PathVariable("owner-id") long ownerId, @PathVariable long id, Authentication authentication) {

        var candidateProfile = candidateProfileService.readById(id);
        candidateProfileService.delete(id);
        log.info("=== DELETE-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("Candidate profile for user %s successfully deleted", candidateProfile.getOwner().getName()));
    }
}
