package com.board.job.config;

import com.board.job.model.dto.login.LoginRequest;
import com.board.job.model.dto.user.UserCreateRequest;
import com.board.job.model.dto.user.UserResponse;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.board.job.config.HelperForTests.asJsonString;
import static com.board.job.config.HelperForTests.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class SecurityConfigTests {
    private final MockMvc mvc;

    private final UserMapper mapper;
    private final RoleService roleService;

    @Autowired
    public SecurityConfigTests(MockMvc mvc, UserMapper mapper, RoleService roleService) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.roleService = roleService;
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(mvc).isNotNull();
        AssertionsForClassTypes.assertThat(mapper).isNotNull();
        AssertionsForClassTypes.assertThat(roleService).isNotNull();
    }

    @Test
    public void test_SecuredUrl_Login() throws Exception {
        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(new LoginRequest("admin@mail.co", "1111"))
                        )
                )
                .andExpect(status().isOk());
    }

    @Test
    public void test_SecuredUrl_Register() throws Exception {
        UserCreateRequest userCreateRequest = createUser( "Firstname", "Lastname",
                "new@mail.co", "1234567890");

        User user = mapper.getUserFromUserCreate(userCreateRequest);
        user.setRoles(Set.of(roleService.readByName("USER")));

        UserResponse expected = mapper.getUserResponseFromUser(user);

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                asJsonString(userCreateRequest)
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals(asJsonString(expected).substring(9),
                        result.getResponse().getContentAsString().substring(9),
                        "This test must be equal, substring for not same id`s, because we create user in this url, so he has not 0 id.")
                );
    }

    @Test
    public void test_SecuredUrl_WhichCannotBeAccessByUnauthorizedUser() throws Exception {
        mvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertEquals(result.getResponse().getErrorMessage(),
                        "Error: Unauthorized (please authorize before going to this URL).",
                        "Here must be a message, for better user understanding, why he can not go to this page.")
                );
    }

    @Test
    public void test_CorsConfig() throws Exception {
        mvc.perform(head("/**")).andExpect(status().isUnauthorized());
    }
}
