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
public class AuthRoleServiceTests {
    private final AuthRolesService authRolesService;

    @Autowired
    public AuthRoleServiceTests(AuthRolesService authRolesService) {
        this.authRolesService = authRolesService;
    }

    @Test
    public void testValidCandidateHasRole() {
        String email = "donald@mail.co";

        assertTrue(authRolesService.hasRole(email, "CANDIDATE"));
    }

    @Test
    public void testValidAdminHasRole() {
        String email = "admin@mail.co";

        assertTrue(authRolesService.hasRole(email, "ADMIN"));
    }

    @Test
    public void testInvalidHasRole() {
        String email = "nikole@mail.co";

        assertFalse(authRolesService.hasRole(email, "ADMIN"));
    }

    @Test
    public void testValidHasAnyRole() {
        String email = "nikole@mail.co";

        assertTrue(authRolesService.hasAnyRole(email, "USER", "CANDIDATE"));
    }

    @Test
    public void testValidHasAnyRoleAdmin() {
        String email = "admin@mail.co";

        assertTrue(authRolesService.hasAnyRole(email,  "CANDIDATE", "EMPLOYER"));
    }

    @Test
    public void testValidHasAnyRoleOneCorrect() {
        String email = "admin@mail.co";

        assertTrue(authRolesService.hasAnyRole(email,  "CANDIDATE", "USER"));
    }

    @Test
    public void testInvalidHasAnyRole() {
        String email = "violet@mail.co";

        assertFalse(authRolesService.hasAnyRole(email,  "ADMIN", "CANDIDATE"));
    }
}
