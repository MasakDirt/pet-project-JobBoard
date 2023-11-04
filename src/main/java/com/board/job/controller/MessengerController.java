package com.board.job.controller;

import com.board.job.model.dto.messenger.CutMessengerResponse;
import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.FeedbackService;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static com.board.job.controller.AuthoritiesHelper.getAuthorities;
import static java.lang.Integer.MIN_VALUE;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}")
public class MessengerController {
    private final UserService userService;
    private final MessengerMapper mapper;
    private final MessengerService messengerService;
    private final FeedbackService feedbackService;

    @GetMapping("/candidate/{candidate-id}/messengers")
    @PreAuthorize("@authCandidateProfileService.isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile" +
            "(#ownerId, #candidateProfileId, authentication.name)")
    public ModelAndView getAllCandidateMessengers(@PathVariable("owner-id") long ownerId,
                                                  @PathVariable("candidate-id") long candidateProfileId,
                                                  Authentication authentication, ModelMap map) {
        var messengers = messengerService.getAllByCandidateProfileId(candidateProfileId)
                .stream()
                .map(messenger -> mapper.getCutMessengerResponseFromMessenger(messenger, feedbackService.getLastFeedbackText(messenger.getId())))
                .toList();
        log.info("=== GET-CANDIDATE-MESSENGERS === {} == {}", getAuthorities(authentication), authentication.getName());
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("messengers", messengers);

        return new ModelAndView("messengers-list", map);
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #vacancyId, authentication.name)")
    public List<CutMessengerResponse> getAllVacancyMessengers(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, Authentication authentication
    ) {
        var responses = messengerService.getAllByVacancyId(vacancyId)
                .stream()
                .map(messenger -> mapper.getCutMessengerResponseFromMessenger(
                                messenger, feedbackService.getLastFeedbackText(messenger.getId())
                        )
                )
                .toList();
        log.info("=== GET-VACANCY-MESSENGERS === {} == {}", getAuthorities(authentication), authentication.getName());

        return responses;
    }

    @PostMapping("/vacancies/{vacancy-id}/candidate/{candidate-id}/messengers")
    @PreAuthorize("hasRole('CANDIDATE')")
    public void createByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("vacancy-id") long vacancyId, String text, HttpServletResponse response,
            Random random, Authentication authentication) throws IOException {

        var name = authentication.getName();
        var messengerId = messengerService.create(vacancyId,
                userService.readByEmail(name).getCandidateProfile().getId()).getId();
        feedbackService.create(userService.readByEmail(authentication.getName()).getId(), messengerId, random.nextInt(MIN_VALUE, 0), text);
        log.info("=== POST-MESSENGER === {} == {}", getAuthorities(authentication), name);

        response.sendRedirect(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks", ownerId, candidateId, messengerId));
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers")
    @PreAuthorize("@authVacancyService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #vacancyId) && hasRole('CANDIDATE')")
    public ResponseEntity<String> createByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, Authentication authentication
    ) {
        var name = authentication.getName();
        var messenger = messengerService.create(vacancyId,
                userService.readByEmail(name).getCandidateProfile().getId());
        log.info("=== POST-MESSENGER === {} == {}", getAuthorities(authentication), name);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Messenger between %s and candidate %s successfully created",
                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                        messenger.getCandidateProfile().getOwner().getName()));
    }

//    @PostMapping("/employer-profile/{employer-id}/candidate-profiles/{candidate-id}/messengers")
//    @PreAuthorize("@authVacancyService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy" +
//            "(#ownerId, #employerId, #vacancyId) && hasRole('CANDIDATE')")
//    public ResponseEntity<String> createByEmployerForCandidateMessengers(
//            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
//            @PathVariable("candidate-id") long candidateId, Authentication authentication
//    ) {
//        var name = authentication.getName();
//        var messenger = messengerService.create(vacancyId,
//                userService.readByEmail(name).getCandidateProfile().getId());
//        log.info("=== POST-MESSENGER === {} == {}", getAuthorities(authentication), name);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(String.format("Messenger between %s and candidate %s successfully created",
//                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
//                        messenger.getCandidateProfile().getOwner().getName()));
//    }

    @GetMapping("/candidate/{candidate-id}/messengers/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger" +
            "(#ownerId, #candidateId, #id, authentication.name)")
    public void deleteByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication, HttpServletResponse response) throws IOException {
        messengerService.delete(id);
        log.info("=== DELETE-MESSENGER-BY-CANDIDATE === {} == {}", getAuthorities(authentication), authentication.getName());

        response.sendRedirect(String.format("/api/users/%s/candidate/%s/messengers",
                ownerId, candidateId));
    }

    @DeleteMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger" +
            "(#ownerId, #employerId, #vacancyId, #id, authentication.name)")
    public ResponseEntity<String> deleteByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable long id, Authentication authentication
    ) {
        var messenger = messengerService.readById(id);
        messengerService.delete(id);
        log.info("=== DELETE-MESSENGER-BY-EMPLOYER === {} == {}", getAuthorities(authentication), authentication.getName());

        return ResponseEntity.ok(
                String.format("Messenger between %s and candidate %s successfully deleted",
                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                        messenger.getCandidateProfile().getOwner().getName())
        );
    }
}
