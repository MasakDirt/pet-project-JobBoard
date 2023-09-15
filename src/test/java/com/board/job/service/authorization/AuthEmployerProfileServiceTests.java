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
public class AuthEmployerProfileServiceTests {
    private final AuthEmployerProfileService authEmployerProfileService;

    @Autowired
    public AuthEmployerProfileServiceTests(AuthEmployerProfileService authEmployerProfileService) {
        this.authEmployerProfileService = authEmployerProfileService;
    }

    @Test
    public void test_Injected_Component() {
        assertNotNull(authEmployerProfileService);
    }

    @Test
    public void test_True_IsUsersSameByIdAndUserOwnerEmployerProfile() {
        assertTrue(authEmployerProfileService
                        .isUsersSameByIdAndUserOwnerEmployerProfile(6L, 3L, "violet@mail.co"),
                "Here must be true because user is sames and she owner of employer profile.");
    }

    @Test
    public void test_False_IsUsersSameByIdAndUserOwnerEmployerProfile() {
        assertFalse(authEmployerProfileService
                        .isUsersSameByIdAndUserOwnerEmployerProfile(6L, 1L, "admin@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authEmployerProfileService
                        .isUsersSameByIdAndUserOwnerEmployerProfile(5L, 3L, "helen@mail.co"),
                "Here must be false because user is not owner of employer profile.");
    }

    @Test
    public void test_True_isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile() {
        assertTrue(authEmployerProfileService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile(-1, 0, "admin@mail.co"),
                "Here must be true because user is admin.");

        assertTrue(authEmployerProfileService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile(2L, 2L, "larry@mail.co"),
                "Here must be true because user is sames and he owner of employer profile.");
    }

    @Test
    public void test_False_isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile() {
        assertFalse(authEmployerProfileService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile(3L, 3L, "violet@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authEmployerProfileService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile(2L, 1L, "larry@mail.co"),
                "Here must be false because user is not owner of employer company.");
    }
}
