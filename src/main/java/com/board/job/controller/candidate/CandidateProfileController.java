package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileResponse;
import com.board.job.model.mapper.candidate.CandidateProfileMapper;
import com.board.job.service.candidate.CandidateProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Set<CandidateProfileResponse> getAll(Authentication authentication) {
        var response = candidateProfileService.getAll()
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .collect(Collectors.toSet());
        log.info("=== GET-CANDIDATE_PROFILES === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/asc")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllByASC(@RequestParam String property, Authentication authentication) {
        var response = candidateProfileService.getAllByASC(property)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-BY-ASC-{} === {} == {}",
                property.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/desc")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllByDESC(@RequestParam String property, Authentication authentication) {
        var response = candidateProfileService.getAllByDESC(property)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-BY-DESC-{} === {} == {}",
                property.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/bigger/salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerSalary(@RequestParam int salary, Authentication authentication) {
        var response = candidateProfileService.getAllBiggerSalaryExpectation(salary)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-BIGGER-SALARY-{} === {} == {}",
                salary, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/smaller/salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllSmallerSalary(@RequestParam int salary, Authentication authentication) {
        var response = candidateProfileService.getAllSmallerSalaryExpectation(salary)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-SMALLER-SALARY-{} === {} == {}",
                salary, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllByCategory(@RequestParam String name, Authentication authentication) {
        var response = candidateProfileService.getAllByCategory(name)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-CATEGORY-{} === {} == {}",
                name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/english")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllByEnglish(@RequestParam String name, Authentication authentication) {
        var response = candidateProfileService.getAllByEnglishLevel(name)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-ENGLISH-{} === {} == {}",
                name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllByWorkExperience(@RequestParam double experience, Authentication authentication) {
        var response = candidateProfileService.getAllByWorkExperience(experience)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-WORK_EXPERIENCE-{} === {} == {}",
                experience, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/country")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllByCountry(@RequestParam String country, Authentication authentication) {
        var response = candidateProfileService.getAllByCountryOfResidence(country)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-COUNTRY-{} === {} == {}",
                country, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/salary-experience")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerSalaryAndWorkExperience(@RequestParam int salary,
                                                                              @RequestParam double experience,
                                                                              Authentication authentication) {

        var response = candidateProfileService.getAllBiggerSalaryExpectationAndWorkExperience(salary, experience)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-SALARY-{}-EXPERIENCE-{} === {} == {}",
                salary, experience, getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-english")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerWorkExperienceAndEnglishLevel(@RequestParam double experience,
                                                                                    @RequestParam String name,
                                                                                    Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndEnglishLevelBigger(experience, name)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-ENGLISH-{} === {} == {}",
                experience, name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerWorkExperienceAndEqualCategory(@RequestParam double experience,
                                                                                     @RequestParam String name,
                                                                                     Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceBiggerAndCategoryEqual(experience, name)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-CATEGORY-{} === {} == {}",
                experience, name.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-country")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerWorkExperienceAndEqualCountry(@RequestParam double experience,
                                                                                    @RequestParam String country,
                                                                                    Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceBiggerAndCountryEqual(experience, country)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-COUNTRY-{} === {} == {}",
                experience, country.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-salary-country")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerWorkExperienceAndSalaryAndEqualCountry(
            @RequestParam double experience, @RequestParam int salary,
            @RequestParam String country, Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndSalaryBiggerAndCountryEqual
                        (experience, salary, country)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-SALARY-{}-COUNTRY-{} === {} == {}",
                experience, salary, country.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-salary-english")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerWorkExperienceAndSalaryAndEnglish(
            @RequestParam double experience, @RequestParam int salary,
            @RequestParam String level, Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndSalaryAndEnglishLevelBigger(
                experience, salary, level)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-SALARY-{}-ENGLISH-{} === {} == {}",
                experience, salary, level.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/experience-salary-category")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public List<CandidateProfileResponse> getAllBiggerWorkExperienceAndSalaryAndCategoryEqual(
            @RequestParam double experience, @RequestParam int salary,
            @RequestParam String category, Authentication authentication) {

        var response = candidateProfileService.getAllWhereWorkExperienceAndSalaryBiggerAndCategoryEqual(
                experience, salary, category)
                .stream()
                .map(mapper::getCandidateProfileResponseFromCandidateProfile)
                .toList();
        log.info("=== GET-CANDIDATE_PROFILES-EXPERIENCE-{}-SALARY-{}-CATEGORY-{} === {} == {}",
                experience, salary, category.toUpperCase(), getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }
}
