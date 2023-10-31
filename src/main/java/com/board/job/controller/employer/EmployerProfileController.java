package com.board.job.controller.employer;

import com.board.job.model.dto.employer_profile.EmployerProfileRequest;
import com.board.job.model.dto.employer_profile.EmployerProfileResponse;
import com.board.job.model.mapper.employer.EmployerProfileMapper;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;
import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/employer-profiles")
public class EmployerProfileController {
    private final EmployerProfileMapper mapper;
    private final EmployerProfileService employerProfileService;

    @GetMapping("/{id}")
    @PreAuthorize("@authEmployerProfileService.isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #id, authentication.name)")
    public EmployerProfileResponse getById(@PathVariable("owner-id") long ownerId,
                                           @PathVariable long id, Authentication authentication) {

        var employerProfile = mapper.getEmployerProfileResponseFromEmployerProfile(employerProfileService.readById(id));
        log.info("=== GET-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getName());

        return employerProfile;
    }

    @PostMapping
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.principal)")
    public ResponseEntity<String> create(@PathVariable("owner-id") long ownerId,
                                         @RequestBody @Valid EmployerProfileRequest request, Authentication authentication) {

        var created = employerProfileService.create(ownerId, mapper.getEmployerProfileFromEmployerProfileRequest(request));
        log.info("=== POST-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return status(HttpStatus.CREATED).body(
                String.format("Employer profile for user %s successfully created", created.getOwner().getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         @RequestBody @Valid EmployerProfileRequest request, Authentication authentication) {

        var updated = employerProfileService.update(id, mapper.getEmployerProfileFromEmployerProfileRequest(request));
        log.info("=== PUT-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok(
                String.format("Employer profile for user %s successfully updated", updated.getOwner().getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         Authentication authentication) {

        var employerProfile = employerProfileService.readById(id);
        employerProfileService.delete(id);
        log.info("=== DELETE-EMPLOYER_PROFILE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ok(
                String.format("Employer profile for user %s successfully deleted", employerProfile.getOwner().getName()));
    }
}
