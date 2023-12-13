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
public class AuthEmployerCompanyServiceTests {
    private final AuthEmployerCompanyService authEmployerCompanyService;

    @Autowired
    public AuthEmployerCompanyServiceTests(AuthEmployerCompanyService authEmployerCompanyService) {
        this.authEmployerCompanyService = authEmployerCompanyService;
    }

    @Test
    public void testInjectedComponent() {
        assertNotNull(authEmployerCompanyService);
    }

    @Test
    public void testTrueIsUsersSameByIdAndUserOwnerEmployerCompany() {
        assertTrue(authEmployerCompanyService
                        .isUsersSameByIdAndUserOwnerEmployerCompany(2L, 2L, "larry@mail.co"),
                "Here must be true because user is sames and he owner of employer company.");
    }

    @Test
    public void testFalseIsUsersSameByIdAndUserOwnerCandidateProfile() {
        assertFalse(authEmployerCompanyService
                        .isUsersSameByIdAndUserOwnerEmployerCompany(2L, 1L, "admin@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authEmployerCompanyService
                        .isUsersSameByIdAndUserOwnerEmployerCompany(2L, 3L, "larry@mail.co"),
                "Here must be false because user is not owner of employer company.");
    }

    @Test
    public void testTrueIsUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile() {
        assertTrue(authEmployerCompanyService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany(0, 0, "admin@mail.co"),
                "Here must be true because user is admin.");

        assertTrue(authEmployerCompanyService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany(6L, 3L, "violet@mail.co"),
                "Here must be true because user is sames and he owner of employer company.");
    }

    @Test
    public void testFalseIsUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile() {
        assertFalse(authEmployerCompanyService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany(3L, 3L, "violet@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authEmployerCompanyService
                        .isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany(6L, 1L, "violet@mail.co"),
                "Here must be false because user is not owner of employer company.");
    }
}
