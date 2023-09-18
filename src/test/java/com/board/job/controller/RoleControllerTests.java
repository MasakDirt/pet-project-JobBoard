package com.board.job.controller;

import com.board.job.model.entity.Role;
import com.board.job.model.mapper.RoleMapper;
import com.board.job.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.board.job.helper.HelperForTests.asJsonString;
import static com.board.job.helper.HelperForTests.getLoginToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RoleControllerTests {
    private final static String BASIC_URL = "/api/roles";
    private final MockMvc mvc;

    private final RoleService roleService;

    private String token;
    private final RoleMapper mapper;

    @Autowired
    public RoleControllerTests(MockMvc mvc, RoleService roleService, RoleMapper mapper) {
        this.mvc = mvc;
        this.roleService = roleService;
        this.mapper = mapper;
    }

    @BeforeEach
    public void initToken() throws Exception {
        token = getLoginToken(mvc, "/api/auth/login", "admin@mail.co", "1111");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(roleService);
        assertNotNull(token);
        assertNotNull(mapper);
    }

    @Test
    public void test_GetAll() throws Exception {
        mvc.perform(get(BASIC_URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("{\"id\":1,\"name\":\"ADMIN\"}") &&
                        result.getResponse().getContentAsString().contains("{\"id\":2,\"name\":\"USER\"}")));
    }

    @Test
    public void test_Invalid_UserIsNotAdmin_GetAll() throws Exception {
        mvc.perform(get(BASIC_URL)
                        .header("Authorization", "Bearer " + getLoginToken(mvc, "/api/auth/login", "helen@mail.co", "5555")))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("\"status\":\"FORBIDDEN\",\"message\":\"Access Denied\"")));
    }

    @Test
    public void test_getAllUserRoles() throws Exception {
        mvc.perform(get(BASIC_URL + "/user/{user-id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("{\"id\":1,\"name\":\"ADMIN\"}") &&
                                result.getResponse().getContentAsString().contains("{\"id\":3,\"name\":\"CANDIDATE\"}") &&
                                result.getResponse().getContentAsString().contains("{\"id\":4,\"name\":\"EMPLOYER\"}"),
                        "This user is admin so he has 3 roles."));
    }

    @Test
    public void test_GetById() throws Exception {
        long id = 3L;
        String expected = asJsonString(mapper.getRoleResponseFromRole(roleService.readById(id)));
        mvc.perform(get(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Invalid_GetById() throws Exception {
        long id = 7L;

        mvc.perform(get(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Role with id not found\"")));
    }

    @Test
    public void test_GetByName() throws Exception {
        String name = "USER";
        String expected = asJsonString(mapper.getRoleResponseFromRole(roleService.readByName(name)));

        mvc.perform(get(BASIC_URL + "/name")
                        .header("Authorization", "Bearer " + token)
                        .param("name", name))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Invalid_Name_GetByName() throws Exception {
        String name = "INVALID";

        mvc.perform(get(BASIC_URL + "/name")
                        .header("Authorization", "Bearer " + token)
                        .param("name", name))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Role with name INVALID not found\"")));
    }

    @Test
    public void test_Create() throws Exception {
        String name = "CREATED";

        mvc.perform(post(BASIC_URL)
                        .header("Authorization", "Bearer " + token)
                        .param("name", name))
                .andExpect(status().isCreated())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("Role with name CREATED created")));
    }

    @Test
    public void test_Update() throws Exception {
        long id = 2L;
        String oldName = roleService.readById(id).getName();
        String name = "UPDATED";

        mvc.perform(put(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .param("name", name)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(String.format("Role old name %s updated to %s", oldName, name))));
    }

    @Test
    public void test_Delete() throws Exception {
        long id = 2L;
        String roleName = roleService.readById(id).getName();
        List<Role> before = roleService.getAll();

        mvc.perform(delete(BASIC_URL + "/{id}", id)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(String.format("Role with name %s successfully deleted", roleName))));

        List<Role> after = roleService.getAll();

        assertTrue(before.size() > after.size());
    }
}
