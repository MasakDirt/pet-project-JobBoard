package com.board.job.controller;

import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.mapper.VacancyMapper;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}")
public class VacancyController {
    private final VacancyMapper mapper;
    private final VacancyService vacancyService;
    private final UserService userService;

    @GetMapping("/vacancies")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public ModelAndView getVacancies(
            @PathVariable("owner-id") long ownerId,
            @RequestParam(name = "sort_by", defaultValue = "id") String[] sortBy,
            @RequestParam(name = "sort_order", defaultValue = "asc") String sortedOrder,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            Authentication authentication, ModelMap map) {

        var sort = Sort.by(Sort.Direction.fromString(sortedOrder), sortBy);
        var pageable = PageRequest.of(page, 5, sort);
        var vacancyPage = vacancyService.getSorted(pageable);
        String sortByString = Arrays.toString(sortBy);

        map.addAttribute("page", page);
        map.addAttribute("vacancies", vacancyPage);
        map.addAttribute("sort_order", sortedOrder);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("sort_by", sortByString.substring(1, sortByString.length() - 1));


        log.info("=== GET-VACANCIES === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("vacancies-list", map);
    }


    @GetMapping("/vacancies/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public FullVacancyResponse getById(@PathVariable("owner-id") long ownerId,
                                       @PathVariable long id, Authentication authentication) {
        var vacancy = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));
        log.info("=== GET-VACANCY === {} == {}", getAuthorities(authentication), authentication.getName());

        return vacancy;
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
