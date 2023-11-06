package com.board.job.controller;

import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.mapper.VacancyMapper;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;
import static com.board.job.controller.HelperForPagesCollections.getSortByValues;

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
    public List<CutVacancyResponse> getAllEmployerVacancies(@PathVariable("owner-id") long ownerId,
                                                            @PathVariable("employer-id") long employerId, Authentication authentication) {
        var responses = vacancyService.getAllByEmployerProfileId(employerId)
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();
        log.info("=== GET-EMPLOYER-VACANCIES === {} == {}", getAuthorities(authentication), authentication.getName());

        return responses;
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{id}")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public FullVacancyResponse getEmployerVacancy(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable long id, Authentication authentication) {

        var responses = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));
        log.info("=== GET-EMPLOYER-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return responses;
    }


    @PostMapping("/employer-profile/{employer-id}/vacancies")
    @PreAuthorize("@authEmployerProfileService.isUsersSameByIdAndUserOwnerEmployerProfile" +
            "(#ownerId, #employerId, authentication.name)")
    public ResponseEntity<String> create(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @RequestBody @Valid VacancyRequest request, Authentication authentication) {

        var vacancy = vacancyService.create(ownerId, mapper.getVacancyFromVacancyRequest(request));
        log.info("=== POST-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Vacancy for employer %s successfully created.", vacancy.getEmployerProfile().getEmployerName()));
    }

    @PutMapping("/employer-profile/{employer-id}/vacancies/{id}")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public ResponseEntity<String> update(
            @PathVariable("owner-id") long ownerId, @PathVariable long id, @PathVariable("employer-id") long employerId,
            @RequestBody @Valid VacancyRequest request, Authentication authentication) {
        var vacancy = vacancyService.update(id, mapper.getVacancyFromVacancyRequest(request));
        log.info("=== PUT-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return ResponseEntity.ok(String.format(
                "Vacancy for employer %s successfully updated.", vacancy.getEmployerProfile().getEmployerName()));
    }

    @DeleteMapping("/employer-profile/{employer-id}/vacancies/{id}")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #id, authentication.name)")
    public ResponseEntity<String> delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         @PathVariable("employer-id") long employerId, Authentication authentication) {

        vacancyService.delete(id);
        log.info("=== DELETE-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return ResponseEntity.ok("Vacancy successfully deleted");
    }
}
