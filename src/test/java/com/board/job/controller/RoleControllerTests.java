package com.board.job.controller;

import com.board.job.model.entity.Role;
import com.board.job.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.board.job.helper.HelperForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RoleControllerTests {
    private final static String BASIC_URL = "/api/roles";
    private final MockMvc mvc;
    private final RoleService roleService;

    @Autowired
    public RoleControllerTests(MockMvc mvc, RoleService roleService) {
        this.mvc = mvc;
        this.roleService = roleService;
    }

    @Test
    public void testInjectedComponents() {
        assertNotNull(mvc);
        assertNotNull(roleService);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetAll() throws Exception {
        mvc.perform(get(BASIC_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roles"))
                .andExpect(view().name("roles-list"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void testInvalidUserIsNotAdminGetAll() throws Exception {
        mvc.perform(get(BASIC_URL))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testgetAllUserRoles() throws Exception {
        mvc.perform(get(BASIC_URL + "/user/{user-id}", 1L))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roles", "userId", "userName"))
                .andExpect(view().name("user-roles-get"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetById() throws Exception {
        long id = 3L;
        mvc.perform(get(BASIC_URL + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("role"))
                .andExpect(view().name("role-get"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testInvalid_GetById() throws Exception {
        long id = 7L;

        mvc.perform(get(BASIC_URL + "/{id}", id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testCreateRequest() throws Exception {
        long id = 3L;
        mvc.perform(get(BASIC_URL + "/create", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roleRequest"))
                .andExpect(view().name("role-create"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testCreate() throws Exception {
        String name = "CREATED";

        mvc.perform(post(BASIC_URL)
                        .param("name", name))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/roles"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testAddUserRoleRequest() throws Exception {
        long userId = 2L;
        mvc.perform(get(BASIC_URL + "/user/{user-id}/add-role", userId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roleRequest", "roles", "userId"))
                .andExpect(view().name("role-user-add"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testAddUserRole() throws Exception {
        String name = "USER";
        long userId = 4L;

        mvc.perform(post(BASIC_URL + "/user/{user-id}/add-role", userId)
                        .param("name", name))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/roles/user/" + userId));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testUpdateRequest() throws Exception {
        long id = 2L;
        String name = "UPDATED";

        mvc.perform(get(BASIC_URL + "/{id}/update", id)
                        .param("name", name)
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("roleRequest", "id"))
                .andExpect(view().name("role-update"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testUpdate() throws Exception {
        long id = 2L;
        String name = "UPDATED";

        mvc.perform(post(BASIC_URL + "/{id}/update", id)
                        .param("name", name)
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/roles"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testDelete() throws Exception {
        long id = 2L;
        List<Role> before = roleService.getAll();

        mvc.perform(get(BASIC_URL + "/{id}/delete", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/roles"));

        List<Role> after = roleService.getAll();

        assertTrue(before.size() > after.size());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testDeleteUserRole() throws Exception {
        long userId = 4L;
        String name = "ADMIN";

        mvc.perform(get(BASIC_URL + "/user/{user-id}/delete-role/{role-name}", userId, name))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/roles/user/" + userId));
    }
}
