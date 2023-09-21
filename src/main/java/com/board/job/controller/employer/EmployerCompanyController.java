package com.board.job.controller.employer;

import com.board.job.model.dto.employer_company.EmployerCompanyResponse;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.mapper.employer.EmployerCompanyMapper;
import com.board.job.service.employer.EmployerCompanyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/employer-companies")
public class EmployerCompanyController {
    private final EmployerCompanyMapper mapper;
    private final EmployerCompanyService employerCompanyService;

    @GetMapping("/{id}")
    @PreAuthorize("@authEmployerCompanyService.isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany" +
            "(#ownerId, #id, authentication.principal)")
    public EmployerCompanyResponse getById(@PathVariable("owner-id") long ownerId,
                                           @PathVariable long id, Authentication authentication) {

        var employerCompany = mapper.getEmployerCompanyResponseFromEmployerCompany(employerCompanyService.readById(id));
        log.info("=== GET-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return employerCompany;
    }

    @PostMapping
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.principal)")
    public ResponseEntity<String> create(@PathVariable("owner-id") long ownerId,
                                         @RequestParam("about_company") String aboutCompany,
                                         @RequestParam("web_site") String webSite, Authentication authentication) {

        var created = employerCompanyService.create(ownerId, aboutCompany, webSite);
        log.info("=== POST-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Employer company for user %s successfully created", created.getOwner().getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authEmployerCompanyService.isUsersSameByIdAndUserOwnerEmployerCompany" +
            "(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         @RequestParam("about_company") String aboutCompany,
                                         @RequestParam("web_site") String webSite, Authentication authentication) {

        var updated = employerCompanyService.update(id, new EmployerCompany(aboutCompany, webSite));
        log.info("=== PUT-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Employer company for user %s successfully updated", updated.getOwner().getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authEmployerCompanyService.isUsersSameByIdAndUserOwnerEmployerCompany" +
            "(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         Authentication authentication) {

        var old = employerCompanyService.readById(id);
        employerCompanyService.delete(id);
        log.info("=== DELETE-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Employer company for user %s successfully deleted", old.getOwner().getName()));
    }
}
