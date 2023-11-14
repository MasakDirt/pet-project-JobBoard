package com.board.job.controller.employer;

import com.board.job.model.dto.employer_profile.EmployerProfileRequest;
import com.board.job.model.mapper.employer.EmployerProfileMapper;
import com.board.job.service.UserService;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/employer-profiles")
public class EmployerProfileController {
    private final UserService userService;
    private final EmployerProfileMapper mapper;
    private final EmployerProfileService employerProfileService;

    @GetMapping("/{id}")
    @PreAuthorize("@authEmployerProfileService.isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #id, authentication.name)")
    public ModelAndView getById(@PathVariable("owner-id") long ownerId,
                                @PathVariable long id, ModelMap map, Authentication authentication) {

        var employerProfile = mapper.getEmployerProfileResponseFromEmployerProfile(employerProfileService.readById(id));
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("employerProfile", employerProfile);
        log.info("=== GET-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/employer-profile-get", map);
    }

    @GetMapping("/create")
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.name)")
    public ModelAndView getCreateForm(@PathVariable("owner-id") long ownerId, ModelMap map) {

        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("employerProfileRequest", new EmployerProfileRequest());

        return new ModelAndView("employers/employer-profile-create", map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.name)")
    public void create(@PathVariable("owner-id") long ownerId,
                       @Valid EmployerProfileRequest request, Authentication authentication,
                       HttpServletResponse response) throws IOException {

        employerProfileService.create(ownerId, mapper.getEmployerProfileFromEmployerProfileRequest(request));
        log.info("=== POST-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect("/api/auth/login");
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #id, authentication.name)")
    public void update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                       @Valid EmployerProfileRequest request, Authentication authentication,
                       HttpServletResponse response) throws IOException {

        employerProfileService.update(id, mapper.getEmployerProfileFromEmployerProfileRequest(request));
        log.info("=== PUT-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d/employer-profiles/%d", ownerId, id));
    }

    @GetMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #id, authentication.name)")
    public void delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                       Authentication authentication, HttpServletResponse response) throws IOException {

        employerProfileService.delete(id);
        log.info("=== DELETE-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%d/employer-profiles/%d", ownerId, id));
    }
}
