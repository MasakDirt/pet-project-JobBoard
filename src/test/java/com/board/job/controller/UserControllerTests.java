package com.board.job.controller;

import com.board.job.model.dto.user.UserUpdateRequest;
import com.board.job.model.dto.user.UserUpdateRequestWithPassword;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.UserMapper;
import com.board.job.service.RoleService;
import com.board.job.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class UserControllerTests {
    private final static String BASIC_URl = "/api/users";

    private final MockMvc mvc;
    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper mapper;

    private String adminToken;
    private String larryToken;
    private String helenToken;

    @Autowired
    public UserControllerTests(MockMvc mvc, UserService userService, RoleService roleService, UserMapper userMapper) {
        this.mvc = mvc;
        this.userService = userService;
        this.roleService = roleService;
        this.mapper = userMapper;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        larryToken = getUserToken(mvc, "larry@mail.co", "2222");
        helenToken = getUserToken(mvc, "helen@mail.co", "5555");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(userService);
        assertNotNull(roleService);
        assertNotNull(mapper);
    }

    @Test
    public void test_GetAll_ADMIN() throws Exception {
        mvc.perform(get(BASIC_URl)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("{\"id\":2,\"email\":\"larry@mail.co\",\"first_name\":\"Larry\",\"last_name\":\"Jackson\"}") &&
                        result.getResponse().getContentAsString()
                                .contains("\"id\":4,\"email\":\"donald@mail.co\",\"first_name\":\"Donald\",\"last_name\":\"Carol\"")));
    }

    @Test
    public void test_Forbidden_GetAll_USER() throws Exception {
        mvc.perform(get(BASIC_URl)
                        .header("Authorization", "Bearer " + larryToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_GetById_ADMIN() throws Exception {
        long id = 4L;
        String expected = asJsonString(mapper.getUserResponseFromUser(userService.readById(id)));

        mvc.perform(get(BASIC_URl + "/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetById_USER() throws Exception {
        long id = 5L;
        String expected = asJsonString(mapper.getUserResponseFromUser(userService.readById(id)));

        mvc.perform(get(BASIC_URl + "/{id}", id)
                        .header("Authorization", "Bearer " + helenToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetById_USER() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 1L)
                        .header("Authorization", "Bearer " + helenToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_GetByEmail_ADMIN() throws Exception {
        String email = "helen@mail.co";
        String expected = asJsonString(mapper.getUserResponseFromUser(userService.readByEmail(email)));

        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", email)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetByEmail_USER() throws Exception {
        String email = "larry@mail.co";
        String expected = asJsonString(mapper.getUserResponseFromUser(userService.readByEmail(email)));

        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", email)
                        .header("Authorization", "Bearer " + larryToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetByEmail_USER() throws Exception {
        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", "admin@mail.co")
                        .header("Authorization", "Bearer " + helenToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Create_ADMIN() throws Exception {
        mvc.perform(post(BASIC_URl)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createUser("Created", "Last", "new@mail.co", "1234")))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("User admin with name Created Last successfully created", result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Create_USER() throws Exception {
        mvc.perform(post(BASIC_URl)
                        .header("Authorization", "Bearer " + larryToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createUser("Forbidden", "Last", "for@mail.co", "0000")))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Update_USER() throws Exception {
        long id = 5L;
        String unexpected = asJsonString(mapper.getUserResponseFromUser(userService.readById(id)));
        UserUpdateRequestWithPassword updateRequest = UserUpdateRequestWithPassword.builder().build();
        updateRequest.setFirstName("New");
        updateRequest.setLastName("Updated");
        updateRequest.setOldPassword("5555");
        updateRequest.setNewPassword("5678");

        mvc.perform(put(BASIC_URl + "/{id}", id)
                        .header("Authorization", "Bearer " + helenToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("User with name New Updated successfully updated", result.getResponse().getContentAsString()));

        String actual = asJsonString(mapper.getUserResponseFromUser(userService.readById(id)));
        assertNotEquals(unexpected, actual);
    }

    @Test
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long id = 6L;
        UserUpdateRequestWithPassword updateRequest = UserUpdateRequestWithPassword.builder().build();
        updateRequest.setFirstName("New");
        updateRequest.setLastName("Updated");
        updateRequest.setOldPassword("5555");
        updateRequest.setNewPassword("5678");

        mvc.perform(put(BASIC_URl + "/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_UpdateNames_USER() throws Exception {
        long id = 2L;
        String unexpected = asJsonString(mapper.getUserResponseFromUser(userService.readById(id)));
        UserUpdateRequest updateRequest = UserUpdateRequest.builder().build();
        updateRequest.setFirstName("New");
        updateRequest.setLastName("Updated");

        mvc.perform(put(BASIC_URl + "/names/{id}", id)
                        .header("Authorization", "Bearer " + larryToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("User with name New Updated successfully updated", result.getResponse().getContentAsString()));

        String actual = asJsonString(mapper.getUserResponseFromUser(userService.readById(id)));
        assertNotEquals(unexpected, actual);
    }

    @Test
    public void test_Forbidden_UpdateNames_ADMIN() throws Exception {
        long id = 6L;
        UserUpdateRequest updateRequest = UserUpdateRequest.builder().build();
        updateRequest.setFirstName("New");
        updateRequest.setLastName("Updated");

        mvc.perform(put(BASIC_URl + "/names/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updateRequest))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Delete_USER() throws Exception {
        long id = 5L;
        User user = userService.readById(id);
        List<User> before = userService.getAll();

        mvc.perform(delete(BASIC_URl + "/{id}", id)
                        .header("Authorization", "Bearer " + helenToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(String.format("User with name %s successfully deleted", user.getName()),
                        result.getResponse().getContentAsString()));
        List<User> after = userService.getAll();

        assertTrue(before.size() > after.size());
    }

    @Test
    public void test_Forbidden_Delete_ADMIN() throws Exception {
        long id = 2L;

        mvc.perform(delete(BASIC_URl + "/{id}", id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }
}
