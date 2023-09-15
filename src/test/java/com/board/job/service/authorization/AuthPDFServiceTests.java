package com.board.job.service.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class AuthPDFServiceTests {
    private final AuthPDFService authPDFService;

    @Autowired
    public AuthPDFServiceTests(AuthPDFService authPDFService) {
        this.authPDFService = authPDFService;
    }

    @Test
    public void test_Injected_Component() {
        assertNotNull(authPDFService);
    }

    @Test
    public void test_True_isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF() {
        assertTrue(authPDFService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF
                                (4L, 3L, 3L, "donald@mail.co"),
                "Here must be true because user is sames and he owner of candidate contact and " +
                        "candidate contact contain pdf.");
    }

    @Test
    public void test_False_isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF() {
        assertFalse(authPDFService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF
                                (6L, 1L, 1, "admin@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authPDFService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF
                                (5L, 3L, 4L, "helen@mail.co"),
                "Here must be false because user is not owner of candidate contact.");

        assertFalse(authPDFService
                        .isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainPDF
                                (5L, 4L, 2L, "helen@mail.co"),
                "Here must be false because candidate contact is not contain pdf.");
    }
}
