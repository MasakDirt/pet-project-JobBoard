package com.board.job.controller;

import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.createUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthControllerTests {
    private static final String BASIC_URL = "/api/auth";
    private final MockMvc mvc;
    private final UserService userService;

    @Autowired
    public AuthControllerTests(MockMvc mvc, UserService userService) {
        this.mvc = mvc;
        this.userService = userService;
    }

    @Test
    public void testInjectedComponents() {
        assertNotNull(mvc);
        assertNotNull(userService);
    }

    @Test
    public void testValidLogin() throws Exception {
        String email = "violet@mail.co";
        String password = "6666";


        mvc.perform(post(BASIC_URL + "/login")
                        .param("email", email)
                        .param("password", password)
                )
                .andExpect(redirectedUrl("/api/users/" + userService.readByEmail(email).getId()));
    }

    @Test
    public void testInvalidPasswordLogin() throws Exception {
        String email = "donald@mail.co";
        String password = "1234";

        mvc.perform(post(BASIC_URL + "/login")
                        .param("email", email)
                        .param("password", password)
                )
                .andExpect(redirectedUrl("/api/auth/login?error"));

    }

    @Test
    public void testInvalidEmailLogin() throws Exception {
        String email = "invalid@mail.co";
        String password = "1111";

        mvc.perform(post(BASIC_URL + "/login")
                        .param("email", email)
                        .param("password", password)
                )
                .andExpect(redirectedUrl("/api/auth/login?error"));
    }

    @Test
    public void testValidRegister() throws Exception {
        UserCreateRequest userCreate = createUser("Sergo", "Test", "test@mail.co", "pass");

        mvc.perform(post(BASIC_URL + "/register")
                        .param("firstName", userCreate.getFirstName())
                        .param("lastName", userCreate.getLastName())
                        .param("email", userCreate.getEmail())
                        .param("password", userCreate.getPassword())
                )
                .andExpect(redirectedUrl("/api/auth/login"));

    }
}
