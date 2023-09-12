package com.board.job.controller;

import com.board.job.model.dto.feedback.FeedbackResponse;
import com.board.job.model.mapper.FeedbackMapper;
import com.board.job.service.FeedbackService;
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
@RequestMapping("/api/users/{owner-id}")
public class FeedbackController {
    private final FeedbackMapper mapper;
    private final FeedbackService feedbackService;

    @GetMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks/{id}")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #candidateId, #messengerId, #id, authentication.principal)")
    public FeedbackResponse getFeedbackByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @PathVariable String id, Authentication authentication
    ) {
        var response = mapper.getFeedbackResponseFromFeedback(feedbackService.readById(id));
        log.info("=== GET-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @GetMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, #id, authentication.principal)")
    public FeedbackResponse getFeedbackByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @PathVariable String id, Authentication authentication
    ) {
        var response = mapper.getFeedbackResponseFromFeedback(feedbackService.readById(id));
        log.info("=== GET-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return response;
    }

    @PostMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger" +
            "(#ownerId, #candidateId, #messengerId, authentication.principal)")
    public ResponseEntity<String> createByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @RequestParam String text, Authentication authentication
    ) {
        feedbackService.create(ownerId, messengerId, text);
        log.info("=== POST-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Feedback with text %s successfully created", text));
    }

    @PostMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks")
    @PreAuthorize("@authMessengerService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, authentication.principal)")
    public ResponseEntity<String> createByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @RequestParam String text, Authentication authentication
    ) {
        feedbackService.create(ownerId, messengerId, text);
        log.info("=== POST-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(String.format("Feedback with text %s successfully created", text));
    }

    @PutMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks/{id}")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #candidateId, #messengerId, #id, authentication.principal)")
    public ResponseEntity<String> updateByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @PathVariable String id,
            @RequestParam String text, Authentication authentication
    ) {
        feedbackService.update(id, text);
        log.info("=== PUT-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Feedback with text %s successfully updated", text));
    }

    @PutMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, #id, authentication.principal)")
    public ResponseEntity<String> updateByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @PathVariable String id, @RequestParam String text, Authentication authentication
    ) {
        feedbackService.update(id, text);
        log.info("=== PUT-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok(String.format("Feedback with text %s successfully updated", text));
    }

    @DeleteMapping("/candidate/{candidate-id}/messengers/{messenger-id}/feedbacks/{id}")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #candidateId, #messengerId, #id, authentication.principal)")
    public ResponseEntity<String> deleteByCandidate(
            @PathVariable("owner-id") long ownerId, @PathVariable("candidate-id") long candidateId,
            @PathVariable("messenger-id") long messengerId, @PathVariable String id, Authentication authentication
    ) {
        feedbackService.delete(id);
        log.info("=== DELETE-CANDIDATE-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("Feedback successfully deleted");
    }

    @DeleteMapping("/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}")
    @PreAuthorize("@authFeedbackService.isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback" +
            "(#ownerId, #employerId, #vacancyId, #messengerId, #id, authentication.principal)")
    public ResponseEntity<String> deleteByEmployer(
            @PathVariable("owner-id") long ownerId, @PathVariable("employer-id") long employerId,
            @PathVariable("vacancy-id") long vacancyId, @PathVariable("messenger-id") long messengerId,
            @PathVariable String id, Authentication authentication
    ) {
        feedbackService.delete(id);
        log.info("=== DELETE-EMPLOYER-FEEDBACK === {} == {}", getAuthorities(authentication), authentication.getPrincipal());

        return ResponseEntity.ok("Feedback successfully deleted");
    }
}
