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
public class UserAuthServiceTests {
    private final UserAuthService userAuthService;

    @Autowired
    public UserAuthServiceTests(UserAuthService authService) {
        this.userAuthService = authService;
    }

    @Test
    public void testInjectedComponent() {
        assertNotNull(userAuthService);
    }

    @Test
    public void testTrueIsUsersSame() {
        assertTrue(userAuthService.isUsersSame(3L, "nikole@mail.co"));
    }

    @Test
    public void testFalseIsUsersSame() {
        assertFalse(userAuthService.isUsersSame(1L, "nikole@mail.co"),
                "Here must be false because user with id 1 has another email.");
    }

    @Test
    public void testTrueIsUserAdminOrUsersSameById() {
        assertTrue(userAuthService.isUserAdminOrUsersSameById(2L, "admin@mail.co"),
                "Here must be true because admin is authorize");
        assertTrue(userAuthService.isUserAdminOrUsersSameById(2L, "larry@mail.co"));
    }

    @Test
    public void testFalseIsUserAdminOrUsersSameById() {
        assertFalse(userAuthService.isUserAdminOrUsersSameById(2L, "helen@mail.co"),
                "Here must be false because user is not same and is not admin");
    }

    @Test
    public void testTrueIsUserAdminOrUsersSameByEmail() {
        assertTrue(userAuthService.isUserAdminOrUsersSameByEmail("", "admin@mail.co"),
                "Here must be true because admin is authorize");
        assertTrue(userAuthService.isUserAdminOrUsersSameByEmail("helen@mail.co", "helen@mail.co"));
    }

    @Test
    public void testFalseIsUserAdminOrUsersSameByEmail() {
        assertFalse(userAuthService.isUserAdminOrUsersSameByEmail("", "helen@mail.co"),
                "Here must be false because our users is not same.");
    }

    @Test
    public void testTrueIsAdmin() {
        assertTrue(userAuthService.isAdmin("admin@mail.co"));
    }

    @Test
    public void testFalseIsAdmin() {
        assertFalse(userAuthService.isAdmin("violet@mail.co"));
    }
}
