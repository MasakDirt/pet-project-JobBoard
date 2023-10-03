package com.board.job.controller;

import com.board.job.model.dto.login.LoginRequest;
import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.UserMapper;
import com.board.job.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.asJsonString;
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
    private final UserMapper mapper;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthControllerTests(MockMvc mvc, UserMapper mapper, JwtUtils jwtUtils) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.jwtUtils = jwtUtils;
    }

        @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(mapper);
        assertNotNull(jwtUtils);
    }

    @Test
    public void test_Valid_Login() throws Exception {
        String email = "violet@mail.co";
        String password = "6666";

        mvc.perform(post(BASIC_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(LoginRequest.of(email, password))
                        )
                )
                .andExpect(status().isOk())
                .andExpectAll(
                        result -> assertTrue(jwtUtils.isValidToken(result.getResponse().getContentAsString()),
                                "If valid token created here must be true."),
                        result -> assertEquals(jwtUtils.getSubject(result.getResponse().getContentAsString()), email,
                                "From token we must get valid user email.")
                );
    }

    @Test
    public void test_Invalid_Password_Login() throws Exception {
        String email = "donald@mail.co";
        String password = "1234";

        mvc.perform(post(BASIC_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(LoginRequest.of(email, password))
                        )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        result -> assertEquals("\"status\":\"UNAUTHORIZED\",\"message\":\"Wrong password\",\"path\":\"http://localhost/api/auth/login\"}",
                                result.getResponse().getContentAsString().substring(35),
                                "Messages when user write wrong password must be sames")
                );
    }

    @Test
    public void test_Valid_Register() throws Exception {
        UserCreateRequest userCreate = createUser("Sergo", "Test", "test@mail.co", "pass");
        User user = mapper.getUserFromUserCreate(userCreate);

        mvc.perform(post(BASIC_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userCreate))
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals(asJsonString(mapper.getUserResponseFromUser(user)).substring(8),
                        result.getResponse().getContentAsString().substring(9))
                );
    }
}
