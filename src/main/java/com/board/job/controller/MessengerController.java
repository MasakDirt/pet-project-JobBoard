package com.board.job.controller;

import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.FeedbackService;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;

import static com.board.job.controller.ControllerHelper.getAuthorities;
import static com.board.job.controller.ControllerHelper.sendRedirectAndCheckForError;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}")
public class MessengerController {
    private final UserService userService;
    private final MessengerMapper mapper;
    private final MessengerService messengerService;
    private final FeedbackService feedbackService;
    private final VacancyService vacancyService;

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
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("messengers", messengers);
        log.info("=== GET-CANDIDATE-MESSENGERS === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("messengers-list", map);
    }

    @GetMapping("/vacancies/{vacancy-id}/employer-profile/{employer-id}/messengers")
    @PreAuthorize("@authVacancyService.isUsersSameAndEmployerProfileOwnerOfVacancy" +
            "(#ownerId, #employerId, #vacancyId, authentication.name)")
    public ModelAndView getAllVacancyMessengers(
            @PathVariable("owner-id") long ownerId, @PathVariable("vacancy-id") long vacancyId,
            @PathVariable("employer-id") long employerId, Authentication authentication, ModelMap map
    ) {
        var messengers = messengerService.getAllByVacancyId(vacancyId)
                .stream()
                .map(messenger -> mapper.getCutMessengerResponseFromMessenger(
                                messenger, feedbackService.getLastFeedbackText(messenger.getId())
                        )
                )
                .toList();

        map.addAttribute("vacancyId", vacancyId);
        map.addAttribute("employerId", employerId);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("messengers", messengers);
        log.info("=== GET-VACANCY-MESSENGERS === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/employer-messengers-list", map);
    }

    @PostMapping("/vacancies/{vacancy-id}/candidate/{candidate-id}/messengers")
    @PreAuthorize("hasRole('CANDIDATE')")
    public void createByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("vacancy-id") long vacancyId, String text, HttpServletResponse response,
            Authentication authentication) {

        var name = authentication.getName();
        var messengerId = messengerService.create(vacancyId,
                userService.readByEmail(name).getCandidateProfile().getId()).getId();
        feedbackService.create(userService.readByEmail(authentication.getName()).getId(), messengerId, text);
        log.info("=== POST-MESSENGER-CANDIDATE === {} == {}", getAuthorities(authentication), name);

        sendRedirectAndCheckForError(response, String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                ownerId, candidateId, messengerId));
    }

    @PostMapping("/vacancies/{vacancy-id}/candidate/{candidate-id}/employer-profile/{employer-id}/messengers")
    @PreAuthorize("hasRole('EMPLOYER')")
    public void createByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("vacancy-id") long vacancyId, Authentication authentication,
            HttpServletResponse response) {
        var name = authentication.getName();
        var messenger = messengerService.create(vacancyId, candidateId);
        log.info("=== POST-MESSENGER-EMPLOYER === {} == {}", getAuthorities(authentication), name);

        sendRedirectAndCheckForError(response, String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                ownerId, vacancyService.readById(vacancyId).getEmployerProfile().getId(), vacancyId, messenger.getId()));
    }

    @GetMapping("/candidate/{candidate-id}/messengers/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger" +
            "(#ownerId, #candidateId, #id, authentication.name)")
    public void deleteByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long id, Authentication authentication, HttpServletResponse response) {
        messengerService.delete(id);
        log.info("=== DELETE-MESSENGER-BY-CANDIDATE === {} == {}", getAuthorities(authentication), authentication.getName());

        sendRedirectAndCheckForError(response, String.format("/api/users/%s/candidate/%s/messengers",
                ownerId, candidateId));
    }

    @GetMapping("/vacancies/{vacancy-id}/employer-profile/{employer-id}/messengers/{id}/delete")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger" +
            "(#ownerId, #employerId, #vacancyId, #id, authentication.name)")
    public void deleteByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable long id,
            Authentication authentication, HttpServletResponse response
    ) {
        messengerService.delete(id);
        log.info("=== DELETE-MESSENGER-BY-EMPLOYER === {} == {}", getAuthorities(authentication), authentication.getName());


        sendRedirectAndCheckForError(response,String.format("/api/users/%s/vacancies/%s/employer-profile/%s/messengers",
                ownerId, vacancyId, employerId));
    }
}
