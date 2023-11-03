package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;
import static com.board.job.controller.HelperForPagesCollections.*;

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
    public ModelAndView getSortedCandidates(
            @RequestParam(name = "sort_by", defaultValue = "id") String[] sortBy,
            @RequestParam(name = "sort_order", defaultValue = "asc") String sortedOrder,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "searchText", required = false, defaultValue = "") String searchText,
            Authentication authentication, ModelMap map) {

        map.addAttribute("page", page);
        map.addAttribute("searchText", searchText);
        map.addAttribute("sort_order", sortedOrder);
        map.addAttribute("sort_by", getSortByValues(Arrays.toString(sortBy)));
        map.addAttribute("owner", userService.readByEmail(authentication.getName()));
        map.addAttribute("candidates", candidateProfileService.getAllSorted(
                        PageRequest.of(page, 5, Sort.by(Sort.Direction.fromString(sortedOrder), sortBy)),
                        searchText
                )
        );
        log.info("=== GET-SORTED-CANDIDATE_PROFILES === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/candidates-list", map);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER') || " +
            "@authCandidateProfileService.isUsersSameByIdAndUserOwnerCandidateProfile(#ownerId, #id, authentication.name)")
    public ModelAndView getById(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                Authentication authentication, ModelMap map) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("candidateProfileRequest", mapper.
                getCandidateProfileRequestFromCandidateProfile(candidateProfileService.readById(id)));
        map.addAttribute("categories", Arrays.stream(Category.values()).map(Category::getValue));
        map.addAttribute("eng_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));
        map.addAttribute("ukr_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));

        log.info("=== GET-CANDIDATE_PROFILE === {} - {}", getAuthorities(authentication), authentication.getName());
        return new ModelAndView("candidates/candidate-profile-get", map);
    }

    @GetMapping("/create")
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.name)")
    public ModelAndView createRequest(@PathVariable("owner-id") long ownerId, ModelMap map) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("candidateProfileRequest", new CandidateProfileRequest());
        map.addAttribute("categories", Arrays.stream(Category.values()).map(Category::getValue));
        map.addAttribute("eng_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));
        map.addAttribute("ukr_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));

        return new ModelAndView("candidates/candidate-profile-create", map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userAuthService.isUserAdminOrUsersSameById(#ownerId, authentication.name)")
    public void create(@PathVariable("owner-id") long ownerId, @Valid CandidateProfileRequest request,
                       Authentication authentication, HttpServletResponse response) throws IOException {

        candidateProfileService.create(ownerId, mapper.getCandidateProfileFromCandidateProfileRequest(request));
        log.info("=== POST-CANDIDATE_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect("/api/auth/login");
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
