package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CutCandidateProfileResponse;
import com.board.job.model.dto.candidate_profile.FullCandidateProfileResponse;
import com.board.job.model.mapper.candidate.CandidateProfileMapper;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/users/{owner-id}/candidate-profile")
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

    @GetMapping("/asc")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllByASC(@RequestParam String property, Authentication authentication) {
        var response = candidateProfileService.getAllByASC(property)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-BY-ASC-{} === {} == {}",
                property.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/desc")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllByDESC(@RequestParam String property, Authentication authentication) {
        var response = candidateProfileService.getAllByDESC(property)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-BY-DESC-{} === {} == {}",
                property.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/bigger/salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerSalary(@RequestParam int salary, Authentication authentication) {
        var response = candidateProfileService.getAllBiggerSalaryExpectation(salary)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-BIGGER-SALARY-{} === {} == {}",
                salary, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/smaller/salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllSmallerSalary(@RequestParam int salary, Authentication authentication) {
        var response = candidateProfileService.getAllSmallerSalaryExpectation(salary)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-SMALLER-SALARY-{} === {} == {}",
                salary, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllByCategory(@RequestParam String name, Authentication authentication) {
        var response = candidateProfileService.getAllByCategory(name)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-CATEGORY-{} === {} == {}",
                name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/english")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllByEnglish(@RequestParam String name, Authentication authentication) {
        var response = candidateProfileService.getAllByEnglishLevel(name)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-ENGLISH-{} === {} == {}",
                name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllByWorkExperience(@RequestParam double experience, Authentication authentication) {
        var response = candidateProfileService.getAllByWorkExperience(experience)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-WORK_EXPERIENCE-{} === {} == {}",
                experience, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/country")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllByCountry(@RequestParam String country, Authentication authentication) {
        var response = candidateProfileService.getAllByCountryOfResidence(country)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-COUNTRY-{} === {} == {}",
                country, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/salary-experience")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerSalaryAndWorkExperience(@RequestParam int salary,
                                                                                  @RequestParam double experience,
                                                                                  Authentication authentication) {

        var response = candidateProfileService.getAllBiggerSalaryExpectationAndWorkExperience(salary, experience)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-SALARY-{}-EXPERIENCE-{} === {} == {}",
                salary, experience, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-english")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerWorkExperienceAndEnglishLevel(@RequestParam double experience,
                                                                                        @RequestParam String name,
                                                                                        Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndEnglishLevelBigger(experience, name)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-ENGLISH-{} === {} == {}",
                experience, name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerWorkExperienceAndEqualCategory(@RequestParam double experience,
                                                                                         @RequestParam String name,
                                                                                         Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceBiggerAndCategoryEqual(experience, name)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-CATEGORY-{} === {} == {}",
                experience, name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-country")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerWorkExperienceAndEqualCountry(@RequestParam double experience,
                                                                                        @RequestParam String country,
                                                                                        Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceBiggerAndCountryEqual(experience, country)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-COUNTRY-{} === {} == {}",
                experience, country.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-salary-country")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerWorkExperienceAndSalaryAndEqualCountry(
            @RequestParam double experience, @RequestParam int salary,
            @RequestParam String country, Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndSalaryBiggerAndCountryEqual
                        (experience, salary, country)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-SALARY-{}-COUNTRY-{} === {} == {}",
                experience, salary, country.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-salary-english")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerWorkExperienceAndSalaryAndEnglish(
            @RequestParam double experience, @RequestParam int salary,
            @RequestParam String level, Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndSalaryAndEnglishLevelBigger(
                experience, salary, level)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-SALARY-{}-ENGLISH-{} === {} == {}",
                experience, salary, level.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-salary-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CutCandidateProfileResponse> getAllBiggerWorkExperienceAndSalaryAndCategoryEqual(
            @RequestParam double experience, @RequestParam int salary,
            @RequestParam String category, Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndSalaryBiggerAndCategoryEqual(
                experience, salary, category)
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-SALARY-{}-CATEGORY-{} === {} == {}",
                experience, salary, category.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
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
