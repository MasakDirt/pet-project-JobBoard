package com.board.job.controller;

import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.FeedbackService;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
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
import java.time.format.DateTimeFormatter;

import static com.board.job.controller.ControllerHelper.getAuthorities;
import static com.board.job.controller.ControllerHelper.redirectionError;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/users/{owner-id}")
public class FeedbackController {
    private final MessengerMapper messengerMapper;
    private final FeedbackService feedbackService;
    private final MessengerService messengerService;
    private final UserService userService;

    @GetMapping("/candidate/{candidate-id}/messengers/{messengerId}/feedbacks")
    @PreAuthorize("@authMessengerService.isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger" +
            "(#ownerId, #candidateId, #messengerId, authentication.name)")
    public ModelAndView getAllCandidateMessengerFeedbacks(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable long messengerId, Authentication authentication, ModelMap map
    ) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        map.addAttribute("messenger", messengerMapper.getFullMessengerResponseFromMessenger(messengerService.readById(messengerId),
                feedbackService.getAllVacancyMessengerFeedbacks(messengerId)));
        log.info("=== GET-CANDIDATE-MESSENGER-FEEDBACKS === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("candidates/feedbacks-list", map);
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}/feedbacks")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger" +
            "(#ownerId, #employerId, #vacancyId, #id, authentication.name)")
    public ModelAndView getAllEmployerMessengerFeedbacks(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable long id, Authentication authentication, ModelMap map) {
        var messenger = messengerMapper.getFullMessengerResponseFromMessenger(messengerService.readById(id),
                feedbackService.getAllVacancyMessengerFeedbacks(id));

        map.addAttribute("messenger", messenger);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("candidate", messenger.getCandidateProfile());
        map.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd MMM"));
        log.info("=== GET-VACANCY-MESSENGER-FEEDBACKS === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/feedbacks-list", map);
    }

    @PostMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger" +
            "(#ownerId, #candidateId, #messengerId, authentication.name)")
    public void createByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, String text, Authentication authentication,
            HttpServletResponse response) {

        feedbackService.create(ownerId, messengerId, text);
        log.info("=== POST-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                    ownerId, candidateId, messengerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, authentication.name)")
    public void createByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @RequestParam String text, Authentication authentication, HttpServletResponse response) {
        feedbackService.create(ownerId, messengerId, text);
        log.info("=== POST-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                    ownerId, employerId, vacancyId, messengerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    @GetMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks/{id}/edit")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #candidateId, #messengerId, #id, authentication.name)")
    public ModelAndView getFormForUpdateByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @PathVariable String id,
            Authentication authentication, ModelMap map) {
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("feedback", feedbackService.readById(id));
        log.info("=== GET-FORM-FOR-UPDATE-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("candidates/feedback-update", map);
    }

    @PostMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks/{id}/edit")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #candidateId, #messengerId, #id, authentication.name)")
    public void updateByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @PathVariable String id,
            String text, Authentication authentication, HttpServletResponse response) {
        feedbackService.update(id, text);
        log.info("=== PUT-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                    ownerId, candidateId, messengerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/edit")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, #id, authentication.name)")
    public ModelAndView getFormForUpdateByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId, @PathVariable String id,
            Authentication authentication, ModelMap map) {
        map.addAttribute("employerId", employerId);
        map.addAttribute("vacancyId", vacancyId);
        map.addAttribute("messengerId", messengerId);
        map.addAttribute("owner", userService.readById(ownerId));
        map.addAttribute("feedback", feedbackService.readById(id));
        log.info("=== GET-FORM-FOR-UPDATE-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        return new ModelAndView("employers/feedback-update", map);
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/edit")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, #id, authentication.name)")
    public void updateByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @PathVariable String id, String text, Authentication authentication, HttpServletResponse response) {
        feedbackService.update(id, text);
        log.info("=== PUT-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                    ownerId, employerId, vacancyId, messengerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }

    }

    @GetMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #candidateId, #messengerId, #id, authentication.name)")
    public void deleteByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @PathVariable String id,
            Authentication authentication, HttpServletResponse response) {
        feedbackService.delete(id);
        log.info("=== DELETE-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                    ownerId, candidateId, messengerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, #id, authentication.name)")
    public void deleteByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @PathVariable String id, Authentication authentication, HttpServletResponse response) {
        feedbackService.delete(id);
        log.info("=== DELETE-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getName());

        try {
            response.sendRedirect(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                    ownerId, employerId, vacancyId, messengerId));
        } catch (IOException e) {
            log.error("Error while sending redirect - {}", e.getMessage());
            redirectionError();
        }
    }
}
