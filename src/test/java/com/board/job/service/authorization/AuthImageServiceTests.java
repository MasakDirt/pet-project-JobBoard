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
public class AuthImageServiceTests {
    private final AuthImageService authImageService;

    @Autowired
    public AuthImageServiceTests(AuthImageService authImageService) {
        this.authImageService = authImageService;
    }

    @Test
    public void test_Injected_Component() {
        assertNotNull(authImageService);
    }

    @Test
    public void test_True_isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage() {
        assertTrue(authImageService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage
                                (3L, 2L, 2, "nikole@mail.co"),
                "Here must be true because user is sames and he owner of candidate contact and " +
                        "candidate contact contain image.");
    }

    @Test
    public void test_False_isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage() {
        assertFalse(authImageService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage
                                (6L, 1L, 1, "admin@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authImageService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage
                                (5L, 3L, 4L, "helen@mail.co"),
                "Here must be false because user is not owner of candidate contact.");

        assertFalse(authImageService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage
                                (5L, 4L, 2L, "helen@mail.co"),
                "Here must be false because candidate contact is not contain image.");
    }

    @Test
    public void test_True_isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage() {
        assertTrue(authImageService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage
                                (1L, 1, 1L, "admin@mail.co"),
                "Here must be true because user is sames and he owner of candidate contact and " +
                        "candidate contact contain image.");
    }

    @Test
    public void test_False_isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage() {
        assertFalse(authImageService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage
                                (3L, 3L, 6L, "violet@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authImageService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage
                                (2L, 1L, 5L,  "larry@mail.co"),
                "Here must be false because user is not owner of employer profile.");

        assertFalse(authImageService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage
                                (2L, 2L, 6L,  "larry@mail.co"),
                "Here must be false because employer profile is not contain image.");
    }
}
