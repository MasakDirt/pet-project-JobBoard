package com.board.job.config;

import com.board.job.service.UserService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTests {
    private final MockMvc mvc;

    private final UserService userService;

    @Autowired
    public SecurityConfigTests(MockMvc mvc, UserService userService) {
        this.mvc = mvc;
        this.userService = userService;
    }

    @Test
    public void testInjectedComponents() {
        AssertionsForClassTypes.assertThat(mvc).isNotNull();
        AssertionsForClassTypes.assertThat(userService).isNotNull();
    }

    @Test
    public void testSecuredUrlLogin() throws Exception {
        String email = "admin@mail.co";

        mvc.perform(post("/api/auth/login")
                        .param("email", email)
                        .param("password", "1111")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/users/" + userService.readByEmail(email).getId()));
    }

    @Test
    public void testSecuredUrlRegister() throws Exception {
        mvc.perform(post("/api/auth/register")
                        .param("firstName", "Firstname")
                        .param("lastName", "Lastname")
                        .param("email", "new@mail.co")
                        .param("password", "1234567890")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/auth/login"));
    }

    @Test
    public void testSecuredUrlWhichCannotBeAccessByUnauthorizedUser() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/api/auth/login"));
    }

    @Test
    public void testCorsConfig() throws Exception {
        mvc.perform(head("/**")).andExpect(status().isFound());
    }
}
