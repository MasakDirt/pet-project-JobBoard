package com.board.job.controller;

import com.board.job.model.dto.messenger.CutMessengerResponse;
import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/users/{owner-id}")
public class MessengerController {
    private final UserService userService;
    private final MessengerMapper mapper;
    private final MessengerService messengerService;

    @GetMapping("/candidate/{candidate-id}/messengers")
    @PreAuthorize("@authCandidateProfileService.isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile" +
            "(#ownerId, #candidateId, authentication.principal)")
    public List<CutMessengerResponse> getAllCandidateMessengers(@PathVariable("owner-id") long ownerId,
                                                                @PathVariable("candidate-id") long candidateId, Authentication authentication) {
        var responses = messengerService.getAllByCandidateId(candidateId)
                .stream()
                .map(mapper::getCutMessengerResponseFromMessenger)
                .toList();
        log.info("=== GET-CANDIDATE-MESSENGERS === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return responses;
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #vacancyId, authentication.principal)")
    public List<CutMessengerResponse> getAllVacancyMessengers(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, Authentication authentication
    ) {
        var responses = messengerService.getAllByVacancyId(vacancyId)
                .stream()
                .map(mapper::getCutMessengerResponseFromMessenger)
                .toList();
        log.info("=== GET-VACANCY-MESSENGERS === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return responses;
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers")
    @PreAuthorize("@authVacancyService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #vacancyId)")
    public ResponseEntity<String> create(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, Authentication authentication
    ) {
        var principal = authentication.getPrincipal();
        var messenger = messengerService.create(vacancyId,
                userService.readByEmail(principal.toString()).getCandidateProfile().getId());
        log.info("=== POST-MESSENGER === {} == {}", getAuthorities(authentication), principal);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Messenger between %s and candidate %s successfully created",
                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                        messenger.getCandidate().getOwner().getName()));
    }

    @DeleteMapping("/candidate/{candidate-id}/messengers/{id}")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger" +
            "(#ownerId, #employerId, #id, authentication.principal)")
    public ResponseEntity<String> deleteByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long employerId,
            @PathVariable long id, Authentication authentication
    ) {
        var messenger = messengerService.readById(id);
        messengerService.delete(id);
        log.info("=== DELETE-MESSENGER-BY-CANDIDATE === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("Messenger between %s and candidate %s successfully deleted",
                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                        messenger.getCandidate().getOwner().getName())
        );
    }

    @DeleteMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger" +
            "(#ownerId, #employerId, #vacancyId, #id, authentication.principal)")
    public ResponseEntity<String> deleteByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable long id, Authentication authentication
    ) {
        var messenger = messengerService.readById(id);
        messengerService.delete(id);
        log.info("=== DELETE-MESSENGER-BY-EMPLOYER === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(
                String.format("Messenger between %s and candidate %s successfully deleted",
                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                        messenger.getCandidate().getOwner().getName())
        );
    }
}
