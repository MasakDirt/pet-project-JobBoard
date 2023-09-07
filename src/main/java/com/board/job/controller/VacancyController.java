package com.board.job.controller;

import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.mapper.VacancyMapper;
import com.board.job.service.VacancyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}/vacancies")
public class VacancyController {
    private final VacancyMapper mapper;
    private final VacancyService vacancyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public List<CutVacancyResponse> getAll(Authentication authentication) {
        var response = vacancyService.getAll()
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();
        log.info("=== GET-VACANCIES === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/sorted")
    @PreAuthorize("hasAnyRole('ADMIN', 'CANDIDATE')")
    public List<CutVacancyResponse> getSortedVacancies(
            @RequestParam(name = "sort_by", defaultValue = "id") String[] sortBy,
            @RequestParam(name = "sort_order", defaultValue = "desc") String sortedOrder,
            @RequestParam(defaultValue = "0") int page, Authentication authentication) {

        var sort = Sort.by(Sort.Direction.fromString(sortedOrder), sortBy);
        Pageable pageable = PageRequest.of(page, 5, sort);

        var vacancies = vacancyService.getSorted(pageable)
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();
        log.info("=== GET-SORTED-VACANCIES === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return vacancies;
    }

    @GetMapping("/{id}")
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.principal)")
    public FullVacancyResponse getById(@PathVariable("owner-id") long ownerId,
                                       @PathVariable long id, Authentication authentication) {
        var vacancy = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));
        log.info("=== GET-VACANCY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return vacancy;
    }

    @PostMapping
    @PreAuthorize("@userAuthService.isUsersSame(#ownerId, authentication.principal)")
    public ResponseEntity<String> create(@PathVariable("owner-id") long ownerId,
                                         @RequestBody @Valid VacancyRequest request, Authentication authentication) {
        var vacancy = vacancyService.create(ownerId, mapper.getVacancyFromVacancyRequest(request));
        log.info("=== POST-VACANCY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Vacancy for employer %s successfully created.", vacancy.getEmployerProfile().getEmployerName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authVacancyService.isUsersSameAndOwnerOfVacancy(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> update(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         @RequestBody @Valid VacancyRequest request, Authentication authentication) {
        var vacancy = vacancyService.update(id, mapper.getVacancyFromVacancyRequest(request));
        log.info("=== PUT-VACANCY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format(
                "Vacancy for employer %s successfully updated.", vacancy.getEmployerProfile().getEmployerName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@authVacancyService.isUsersSameAndOwnerOfVacancy(#ownerId, #id, authentication.principal)")
    public ResponseEntity<String> delete(@PathVariable("owner-id") long ownerId, @PathVariable long id,
                                         Authentication authentication) {

        vacancyService.delete(id);
        log.info("=== DELETE-VACANCY === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("Vacancy successfully deleted");
    }
}
