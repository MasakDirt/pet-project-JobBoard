package com.board.job.service.authorization;

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
public class AuthMessengerServiceTestsForVacanciesReply {
    private final AuthMessengerService authMessengerService;

    @Autowired
    public AuthMessengerServiceTestsForVacanciesReply(AuthMessengerService authMessengerService) {
        this.authMessengerService = authMessengerService;
    }

    @Test
    public void test_Injected_Component() {
        assertNotNull(authMessengerService);
    }

    @Test
    public void test_True_isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger() {
        assertTrue(authMessengerService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (0, 0, 0, "admin@mail.co"),
                "Here must be true because user is admin.");

        assertTrue(authMessengerService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (3L, 2L, 2L, "nikole@mail.co"),
                "Here must be true because user is sames and he owner of candidate profile and " +
                        "candidate profile contain messenger.");
    }

    @Test
    public void test_False_isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger() {
        assertFalse(authMessengerService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (1L, 4L, 4L, "helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authMessengerService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (3L, 3L, 2L, "nikole@mail.co"),
                "Here must be false because user is not owner of candidate profile.");

        assertFalse(authMessengerService
                .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                        (4L, 3L, 2L, "donald@mail.co"),
                "Here must be false because candidate profile is not contain messenger");

    }

    @Test
    public void test_True_isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger() {
        assertTrue(authMessengerService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (3L, 2L, 2L, "nikole@mail.co"),
                "Here must be true because user is sames and he owner of candidate profile and " +
                        "candidate profile contain messenger.");
    }

    @Test
    public void test_False_isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger() {
        assertFalse(authMessengerService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (1L, 4L, 4L, "helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authMessengerService
                        .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                                (3L, 3L, 2L, "nikole@mail.co"),
                "Here must be false because user is not owner of candidate profile.");

        assertFalse(authMessengerService
                .isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger
                        (4L, 3L, 2L, "donald@mail.co"),
                "Here must be false because candidate profile is not contain messenger");

    }

    @Test
    public void test_True_isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger() {
        assertTrue(authMessengerService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger
                                (6L, 3L, 8L, 1L, "violet@mail.co"),
                "Here must be true because user is sames and he owner of employer profile and " +
                        "employer profile and employer profile contain messenger.");
    }

    @Test
    public void test_False_isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger() {
        assertFalse(authMessengerService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger
                                (1L, 2L, 5L, 4L,"helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authMessengerService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger
                                (1L, 2L, 8L, 1L, "admin@mail.co"),
                "Here must be false because user is not owner of employer profile.");

        assertFalse(authMessengerService
                .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger
                        (6L, 3L, 2L, 4L,  "violet@mail.co"),
                "Here must be false because employer profile is not contain vacancy");

        assertFalse(authMessengerService
                .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger
                        (6L, 3L, 7L, 4L,  "violet@mail.co"),
                "Here must be false because vacancy is not contain messenger");

    }
}
