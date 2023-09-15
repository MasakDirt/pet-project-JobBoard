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
public class AuthCandidateContactServiceTests {
    private final AuthCandidateContactService authCandidateContactService;

    @Autowired
    public AuthCandidateContactServiceTests(AuthCandidateContactService authCandidateContactService) {
        this.authCandidateContactService = authCandidateContactService;
    }

    @Test
    public void test_Injected_Component() {
        assertNotNull(authCandidateContactService);
    }

    @Test
    public void test_True_isUsersSameByIdAndUserOwnerCandidateContacts() {
        assertTrue(authCandidateContactService
                        .isUsersSameByIdAndUserOwnerCandidateContacts(3L, 2L, "nikole@mail.co"),
                "Here must be true because user is sames and she owner of candidate contacts.");
    }

    @Test
    public void test_False_isUsersSameByIdAndUserOwnerCandidateContacts() {
        assertFalse(authCandidateContactService
                        .isUsersSameByIdAndUserOwnerCandidateContacts(1L, 3L, "donald@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authCandidateContactService
                        .isUsersSameByIdAndUserOwnerCandidateContacts(4L, 2L, "donald@mail.co"),
                "Here must be false because user is not owner of candidate contacts.");
    }

    @Test
    public void test_True_isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts() {
        assertTrue(authCandidateContactService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(0, 0, "admin@mail.co"),
                "Here must be true because user is admin.");

        assertTrue(authCandidateContactService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(5L, 4L, "helen@mail.co"),
                "Here must be true because user is sames and she owner of candidate contacts.");
    }

    @Test
    public void test_False_isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts() {
        assertFalse(authCandidateContactService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(1L, 4L, "helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authCandidateContactService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(5L, 1L, "helen@mail.co"),
                "Here must be false because user is not owner of candidate contacts.");
    }

}
