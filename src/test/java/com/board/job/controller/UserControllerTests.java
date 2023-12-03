package com.board.job.controller;

import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserControllerTests {
    private final static String BASIC_URl = "/api/users";

    private final MockMvc mvc;
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper mapper;

    @Autowired
    public UserControllerTests(MockMvc mvc, UserService userService, RoleService roleService, UserMapper userMapper) {
        this.mvc = mvc;
        this.userService = userService;
        this.roleService = roleService;
        this.mapper = userMapper;
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(userService);
        assertNotNull(roleService);
        assertNotNull(mapper);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetAll_ADMIN() throws Exception {
        mvc.perform(get(BASIC_URl))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("users-list"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetAll_USER() throws Exception {
        mvc.perform(get(BASIC_URl))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetById_ADMIN() throws Exception {
        long id = 4L;

        mvc.perform(get(BASIC_URl + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_GetById_USER() throws Exception {
        long id = 5L;

        mvc.perform(get(BASIC_URl + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetById_USER() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 1L))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_NotFound_GetById_ADMIN() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 10L))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetByEmail_ADMIN() throws Exception {
        String email = "helen@mail.co";

        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", email)
                )
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetByEmail_USER() throws Exception {
        String email = "larry@mail.co";

        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", email))
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_GetByEmail_USER() throws Exception {
        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", "admin@mail.co")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_NotFound_GetByEmail_ADMIN() throws Exception {
        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", "notfound@mail.co")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetCreateRequest_Admin() throws Exception {
        String email = "larry@mail.co";

        mvc.perform(get(BASIC_URl + "/create-admin")
                        .param("email", email))
                .andExpect(model().attributeExists("createRequest"))
                .andExpect(view().name("user-create"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_Create_ADMIN() throws Exception {
        mvc.perform(post(BASIC_URl)
                        .param("firstName", "Created")
                        .param("lastName", "Last")
                        .param("email", "new@mail.co")
                        .param("password", "1234")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/users/" + 7));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_Create_USER() throws Exception {
        mvc.perform(post(BASIC_URl)
                        .param("firstName", "Forbidden")
                        .param("lastName", "Last")
                        .param("email", "for@mail.co")
                        .param("password", "0000")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetUpdateRequest_Admin() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}/update", 1L))
                .andExpect(model().attributeExists("id", "updateRequest"))
                .andExpect(view().name("user-update"));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Update_USER() throws Exception {
        long id = 5L;

        mvc.perform(post(BASIC_URl + "/{id}/update", id)
                        .param("firstName", "New")
                        .param("lastName", "Updated")
                        .param("oldPassword", "5555")
                        .param("newPassword", "1234")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/users/" + id));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long id = 6L;

        mvc.perform(post(BASIC_URl + "/{id}/update", id)
                        .param("firstName", "New")
                        .param("lastName", "Updated")
                        .param("oldPassword", "5555")
                        .param("newPassword", "1234")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetUpdateNamesRequest_Admin() throws Exception {
        mvc.perform(get(BASIC_URl + "/names/{id}/update", 1L))
                .andExpect(model().attributeExists("id", "updateRequest"))
                .andExpect(view().name("user-update-names"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_UpdateNames_USER() throws Exception {
        long id = 2L;

        mvc.perform(post(BASIC_URl + "/names/{id}/update", id)
                        .param("firstName", "New")
                        .param("lastName", "Updated")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/users/" + id));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_UpdateNames_USER() throws Exception {
        long id = 6L;

        mvc.perform(get(BASIC_URl + "/names/{id}/update", id)
                        .param("firstName", "New")
                        .param("lastName", "Updated")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Delete_USER() throws Exception {
        long id = 5L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/auth/login"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_Delete_USER() throws Exception {
        long id = 4L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
