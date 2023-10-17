package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CutCandidateProfileResponse;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.mapper.candidate.CandidateProfileMapper;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;
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
    private final UserService userService;
    private final CandidateProfileService candidateProfileService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public Set<CutCandidateProfileResponse> getAll(Authentication authentication) {
        var response = candidateProfileService.getAll()
                .stream()
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .collect(Collectors.toSet());
        log.info("=== GET-CANDIDATE_PROFILES === {} == {}", getAuthorities(authentication), authentication.getName());

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
        log.info("=== GET-SORTED-CANDIDATE_PROFILES === {} == {}", getAuthorities(authentication), authentication.getName());

        return candidateProfiles;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER') || " +
            "@authCandidateProfileService.isUsersSameByIdAndUserOwnerCandidateProfile(#ownerId, #id, authentication.name)")
    public ModelAndView getById(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                Authentication authentication, ModelMap map) {
        log.info("=== GET-CANDIDATE_PROFILE === {} - {}", getAuthorities(authentication), authentication.getName());
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("candidateProfileRequest", mapper.
                getCandidateProfileRequestFromCandidateProfile(candidateProfileService.readById(id)));

        map.addAttribute("categories", Arrays.stream(Category.values()).map(Category::getValue));
        map.addAttribute("eng_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));
        map.addAttribute("ukr_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));

        return new ModelAndView("candidate-profile-get", map);
    }

    @GetMapping("/create")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.name)")
    public ModelAndView createRequest(@PathVariable("owner-id") long ownerId, ModelMap map) {
        map.addAttribute("ownerId", ownerId);
        map.addAttribute("candidateProfileRequest",new CandidateProfileRequest());
        map.addAttribute("categories", Arrays.stream(Category.values()).map(Category::getValue));
        map.addAttribute("eng_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));
        map.addAttribute("ukr_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));

        return new ModelAndView("candidate-profile-create", map);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.name)")
    public void create(
            @PathVariable("owner-id") long ownerId, @Valid CandidateProfileRequest request,
            Authentication authentication, HttpServletResponse response) throws IOException {

        var created = candidateProfileService.create(ownerId, mapper.getCandidateProfileFromCandidateProfileRequest(request));
        log.info("=== POST-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d/candidate-profiles/%d", ownerId, created.getId()));
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@authCandidateProfileService.isUsersSameByIdAndUserOwnerCandidateProfile(#ownerId, #id, authentication.name)")
    public void update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                       @Valid CandidateProfileRequest request, Authentication authentication,
                       HttpServletResponse response) throws Exception {

        candidateProfileService.update(id, mapper.getCandidateProfileFromCandidateProfileRequest(request));
        log.info("=== PUT-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d/candidate-profiles/%d", ownerId, id));
    }

    @GetMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authCandidateProfileService.isUsersSameByIdAndUserOwnerCandidateProfile(#ownerId, #id, authentication.name)")
    public void delete(
            @PathVariable("owner-id") long ownerId, @PathVariable long id, Authentication authentication,
            HttpServletResponse response) throws Exception {
        candidateProfileService.delete(id);
        log.info("=== DELETE-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d", ownerId));
    }
}
