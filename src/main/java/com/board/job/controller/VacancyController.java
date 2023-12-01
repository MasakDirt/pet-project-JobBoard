package com.board.job.controller;

import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import com.board.job.model.mapper.VacancyMapper;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static com.board.job.controller.ControllerHelper.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}")
public class VacancyController {
    private final VacancyMapper mapper;
    private final VacancyService vacancyService;
    private final UserService userService;
    private final MessengerService messengerService;

    @GetMapping("/vacancies")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ModelAndView getVacancies(
            @PathVariable("owner-id") long ownerId,
            @RequestParam(name = "sort_by", defaultValue = "postedAt") String[] sortBy,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sortedOrder,
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNumber,
            @RequestParam(name = "searchText", required = false, defaultValue = "") String searchText,
            Authentication authentication, ModelMap map) {

        map.addAttribute("page", pageNumber);
        map.addAttribute("searchText", searchText);
        map.addAttribute("sort_order", sortedOrder);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("sort_by", getSortByValues(Arrays.toString(sortBy)));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("vacancies", vacancyService.getSortedVacancies(
                PageRequest.of(
                        pageNumber, 5, Sort.by(Sort.Direction.fromString(sortedOrder), sortBy)
                ), searchText)
        );
        log.info("=== GET-VACANCIES === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("candidates/vacancies-list", map);
    }

    @GetMapping("/vacancies/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ModelAndView getById(@PathVariable("owner-id") long ownerId,
                                @PathVariable long id, Authentication authentication, ModelMap map) {
        var user = userService.readByEmail(authentication.getName());
        var vacancy = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));

        map.addAttribute("owner", user);
        map.addAttribute("vacancy", vacancy);
        map.addAttribute("messenger", messengerService.readByOwnerAndVacancy(user.getId(), id));
        log.info("=== GET-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("candidates/vacancy-get", map);
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies")
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.name)")
    public ModelAndView getAllEmployerVacancies(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            Authentication authentication, ModelMap map) {
        var responses = vacancyService.getAllByEmployerProfileId(employerId)
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("vacancies", responses);
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        log.info("=== GET-EMPLOYER-VACANCIES === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/company-vacancies-list");
    }

    @GetMapping("/employer-profiles/{employer-id}/vacancies/candidate/{candidate-id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ModelAndView getAllForSelect(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("candidate-id") long candidateId, Authentication authentication, ModelMap map) {

        map.addAttribute("candidateId", candidateId);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("vacancies", vacancyService.getAllByEmployerProfileId(employerId));
        log.info("=== GET-VACANCIES-FOR-EMPLOYER-SELECTION === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/vacancies-for-select", map);
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{id}")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public ModelAndView getEmployerVacancy(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable long id, Authentication authentication, ModelMap map) {
        map.addAttribute("vacancy", mapper.getVacancyRequestFromVacancy(vacancyService.readById(id)));
        addAttributesForGetForms(map, ownerId);
        log.info("=== GET-EMPLOYER-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/vacancy-get", map);
    }


    @GetMapping("/employer-profile/{employer-id}/vacancies/create")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #employerId, authentication.name)")
    public ModelAndView getCreateForm(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            ModelMap map) {
        map.addAttribute("vacancy", new VacancyRequest());
        addAttributesForGetForms(map, ownerId);

        return new ModelAndView("employers/vacancy-create", map);
    }

    private void addAttributesForGetForms(ModelMap map, long ownerId) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("domains", Arrays.stream(JobDomain.values()).map(JobDomain::getValue));
        map.addAttribute("eng_levels", Arrays.stream(LanguageLevel.values()).map(LanguageLevel::getValue));
        map.addAttribute("categories", Arrays.stream(Category.values()).map(Category::getValue));
        map.addAttribute("workModes", Arrays.stream(WorkMode.values()).map(WorkMode::getValue));
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #employerId, authentication.name)")
    public void create(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @Valid VacancyRequest request, Authentication authentication, HttpServletResponse response) throws IOException {

        vacancyService.create(ownerId, mapper.getVacancyFromVacancyRequest(request));
        log.info("=== POST-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%s/employer-profile/%s/vacancies", ownerId, employerId));
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies/{id}/update")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public void update(
            @PathVariable("owner-id") long ownerId, @PathVariable long id, @PathVariable("employer-id") long employerId,
            @Valid VacancyRequest request, Authentication authentication, HttpServletResponse response) throws Exception {
        vacancyService.update(id, mapper.getVacancyFromVacancyRequest(request));
        log.info("=== PUT-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%s/employer-profile/%s/vacancies/%s",
                ownerId, employerId, id));
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{id}/delete")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public void delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                       @PathVariable("employer-id") long employerId, Authentication authentication,
                       HttpServletResponse response) throws Exception {

        vacancyService.delete(id);
        log.info("=== DELETE-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%s/employer-profile/%s/vacancies", ownerId, employerId));
    }
}
