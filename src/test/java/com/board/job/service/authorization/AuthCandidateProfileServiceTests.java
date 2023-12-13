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
public class AuthCandidateProfileServiceTests {
    private final AuthCandidateProfileService authCandidateProfileService;

    @Autowired
    public AuthCandidateProfileServiceTests(AuthCandidateProfileService authCandidateProfileService) {
        this.authCandidateProfileService = authCandidateProfileService;
    }

    @Test
    public void testInjectedComponent() {
        assertNotNull(authCandidateProfileService);
    }

    @Test
    public void testTrueIsUsersSameByIdAndUserOwnerCandidateProfile() {
        assertTrue(authCandidateProfileService
                .isUsersSameByIdAndUserOwnerCandidateProfile(1L, 1L, "admin@mail.co"),
                "Here must be true because user is sames and he owner of candidate profile.");
    }

    @Test
    public void testFalseIsUsersSameByIdAndUserOwnerCandidateProfile() {
        assertFalse(authCandidateProfileService
                .isUsersSameByIdAndUserOwnerCandidateProfile(2L, 1L, "admin@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authCandidateProfileService
                .isUsersSameByIdAndUserOwnerCandidateProfile(1L, 3L, "admin@mail.co"),
                "Here must be false because user is not owner of candidate profile.");
    }

    @Test
    public void testTrueIsUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile() {
        assertTrue(authCandidateProfileService
                .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile(0, 0, "admin@mail.co"),
                "Here must be true because user is admin.");

        assertTrue(authCandidateProfileService
                        .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile(3L, 2L, "nikole@mail.co"),
                "Here must be true because user is sames and he owner of candidate profile.");
    }

    @Test
    public void testFalseIsUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile() {
        assertFalse(authCandidateProfileService
                .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile(3L, 4L, "helen@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authCandidateProfileService
                .isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile(5L, 3L, "helen@mail.co"),
                "Here must be false because user is not owner of candidate profile.");
    }
}
