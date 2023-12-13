package com.board.job.service.authorization;

import com.board.job.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthFeedbackServiceTests {
    private final AuthFeedbackService authFeedbackService;
    private final FeedbackService feedbackService;

    @Autowired
    public AuthFeedbackServiceTests(AuthFeedbackService authFeedbackService, FeedbackService feedbackService) {
        this.authFeedbackService = authFeedbackService;
        this.feedbackService = feedbackService;
    }

    @Test
    public void testInjected_Component() {
        assertNotNull(authFeedbackService);
        assertNotNull(feedbackService);
    }

    @Test
    public void testTrueIsUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback() {
        long ownerId = 3L;
        long messengerId = 2L;
        String feedbackId = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream()
                .filter(feedback -> feedback.getOwnerId() == ownerId)
                .findFirst()
                .get()
                .getId();

        assertTrue(authFeedbackService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (ownerId, 2L, messengerId, feedbackId, "nikole@mail.co"),
                "Here must be true because user is sames and he owner of candidate profile and " +
                        "candidate profile contain messenger and messenger contain feedback.");
    }

    @Test
    public void testFalseIsUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback() {
        long messengerId = 2L;
        String feedbackId = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream()
                .findFirst()
                .get()
                .getId();

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (1L, 4L, 4L, feedbackId, "helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (3L, 3L, 2L, feedbackId, "nikole@mail.co"),
                "Here must be false because user is not owner of candidate profile.");

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (4L, 3L, 2L, feedbackId, "donald@mail.co"),
                "Here must be false because candidate profile is not contain messenger");
    }

    @Test
    public void testTrueIsUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback() {
        long messengerId = 1L;
        String feedbackId = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream()
                .findFirst()
                .get()
                .getId();

        assertTrue(authFeedbackService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (6L, 3L, 8L, messengerId, feedbackId, "violet@mail.co"),
                "Here must be true because user is sames and he owner of employer profile and " +
                        "employer profile and employer profile contain messenger and messenger contain feedback.");
    }

    @Test
    public void testFalseIsUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback() {
        long messengerId = 1L;
        String feedbackId = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream()
                .findFirst()
                .get()
                .getId();

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (1L, 2L, 5L, 4L, feedbackId, "helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (1L, 2L, 8L, 1L, feedbackId,  "admin@mail.co"),
                "Here must be false because user is not owner of employer profile.");

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (6L, 3L, 2L, 4L, feedbackId, "violet@mail.co"),
                "Here must be false because employer profile is not contain vacancy");

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (6L, 3L, 7L, 4L, feedbackId, "violet@mail.co"),
                "Here must be false because vacancy is not contain messenger");

        assertFalse(authFeedbackService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback
                                (6L, 3L, 7L, 2L, feedbackId, "violet@mail.co"),
                "Here must be false because messenger is not contain feedback");

    }
}
