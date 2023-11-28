package com.board.job.controller.employer;

import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.mapper.employer.EmployerCompanyMapper;
import com.board.job.service.UserService;
import com.board.job.service.employer.EmployerCompanyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

import static com.board.job.controller.ControllerHelper.getAuthorities;
import static com.board.job.controller.ControllerHelper.redirectionError;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/employer-companies")
public class EmployerCompanyController {
    private final EmployerCompanyMapper mapper;
    private final UserService userService;
    private final EmployerCompanyService employerCompanyService;

    @GetMapping("/{id}")
    @PreAuthorize("@authEmployerCompanyService.isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany" +
            "(#ownerId, #id, authentication.name)")
    public ModelAndView getById(@PathVariable("owner-id") long ownerId,
                                @PathVariable long id, Authentication authentication, ModelMap map) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("employerCompany",
                mapper.getEmployerCompanyResponseFromEmployerCompany(employerCompanyService.readById(id)));
        log.info("=== GET-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/employer-company-get", map);
    }

    @GetMapping("/create")
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.name)")
    public ModelAndView getFormForCreate(@PathVariable("owner-id") long ownerId, ModelMap map) {

        map.addAttribute("webSite", "");
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("aboutCompany", "");
        return new ModelAndView("employers/employer-company-create", map);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.name)")
    public void create(@PathVariable("owner-id") long ownerId, String aboutCompany, String webSite,
                       Authentication authentication, HttpServletResponse response) {
        long id = employerCompanyService.create(ownerId, aboutCompany, webSite).getId();
        log.info("=== POST-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%d/employer-companies/%d", ownerId, id));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    @PostMapping("/{id}/update")
    @PreAuthorize("@authEmployerCompanyService.isUsersSameByIdAndUserOwnerEmployerCompany" +
            "(#ownerId, #id, authentication.name)")
    public void update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                       String aboutCompany, String webSite, Authentication authentication,
                       HttpServletResponse response) {

        employerCompanyService.update(id, new EmployerCompany(aboutCompany, webSite));
        log.info("=== PUT-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%d/employer-companies/%d", ownerId, id));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("@authEmployerCompanyService.isUsersSameByIdAndUserOwnerEmployerCompany" +
            "(#ownerId, #id, authentication.name)")
    public void delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                       Authentication authentication, HttpServletResponse response) throws IOException {

        employerCompanyService.delete(id);
        log.info("=== DELETE-EMPLOYER_COMPANY === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%d", ownerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }
}
