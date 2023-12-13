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
public class AuthVacancyServiceTests {
    private final AuthVacancyService authVacancyService;

    @Autowired
    public AuthVacancyServiceTests(AuthVacancyService authVacancyService) {
        this.authVacancyService = authVacancyService;
    }

    @Test
    public void testInjectedComponent() {
        assertNotNull(authVacancyService);
    }

    @Test
    public void testTrueIsUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy() {
        assertTrue(authVacancyService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy
                                (2L, 2L, 4L),
                "Here must be true because user is sames and he owner of employer profile and " +
                        "employer profile contain vacancy.");
    }

    @Test
    public void testFalseIsUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy() {
        assertFalse(authVacancyService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy
                                (6L, 1L, 1),
                "Here must be false because user is not sames.");

        assertFalse(authVacancyService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy
                                (2L, 3L, 4L),
                "Here must be false because user is not owner of employer profile.");

        assertFalse(authVacancyService
                        .isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy
                                (2L, 2L, 1L),
                "Here must be false because employer profile is not contain vacancy.");
    }

    @Test
    public void testTrueIsUsersSameAndEmployerProfileOwnerOfVacancy() {
        assertTrue(authVacancyService
                        .isUsersSameAndEmployerProfileOwnerOfVacancy
                                (1L, 1L, 1L, "admin@mail.co"),
                "Here must be true because user is sames and he owner of employer profile and " +
                        "candidate contact contain vacancy.");
    }

    @Test
    public void testFalseIsUsersSameAndEmployerProfileOwnerOfVacancy() {
        assertFalse(authVacancyService
                        .isUsersSameAndEmployerProfileOwnerOfVacancy
                                (6L, 1L, 1, "admin@mail.co"),
                "Here must be false because user is not sames.");

        assertFalse(authVacancyService
                        .isUsersSameAndEmployerProfileOwnerOfVacancy
                                (2L, 3L, 4L, "larry@mail.co"),
                "Here must be false because user is not owner of employer profile.");

        assertFalse(authVacancyService
                        .isUsersSameAndEmployerProfileOwnerOfVacancy
                                (2L, 2L, 1L, "larry@mail.co"),
                "Here must be false because employer profile is not contain vacancy.");
    }
}
