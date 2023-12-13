package com.board.job.controller;

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

    @Autowired
    public UserControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetAllADMIN() throws Exception {
        mvc.perform(get(BASIC_URl))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("users-list"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenGetAllUSER() throws Exception {
        mvc.perform(get(BASIC_URl))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetByIdADMIN() throws Exception {
        long id = 4L;

        mvc.perform(get(BASIC_URl + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user", "isAdmin", "isGoogleUser"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void testGetByIdUSER() throws Exception {
        long id = 5L;

        mvc.perform(get(BASIC_URl + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenGetByIdUSER() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 1L))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testNotFoundGetByIdADMIN() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 10L))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetByEmailADMIN() throws Exception {
        String email = "helen@mail.co";

        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", email)
                )
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testGetByEmailUSER() throws Exception {
        String email = "larry@mail.co";

        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", email))
                .andExpect(model().attributeExists("user", "isAdmin"))
                .andExpect(view().name("user-get"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenGetByEmailUSER() throws Exception {
        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", "admin@mail.co")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testNotFoundGetByEmailADMIN() throws Exception {
        mvc.perform(get(BASIC_URl + "/email")
                        .param("email", "notfound@mail.co")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetCreateRequestAdmin() throws Exception {
        String email = "larry@mail.co";

        mvc.perform(get(BASIC_URl + "/create-admin")
                        .param("email", email))
                .andExpect(model().attributeExists("createRequest"))
                .andExpect(view().name("user-create"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testCreateADMIN() throws Exception {
        mvc.perform(post(BASIC_URl)
                        .param("firstName", "Created")
                        .param("lastName", "Last")
                        .param("email", "new@mail.co")
                        .param("password", "1234")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/users/" + 9));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenCreateUSER() throws Exception {
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
    public void testGetUpdateRequestAdmin() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}/update", 1L))
                .andExpect(model().attributeExists("id", "updateRequest"))
                .andExpect(view().name("user-update"));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void testUpdateUSER() throws Exception {
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
    public void testForbiddenUpdateADMIN() throws Exception {
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
    public void testGetUpdateNamesRequestAdmin() throws Exception {
        mvc.perform(get(BASIC_URl + "/names/{id}/update", 1L))
                .andExpect(model().attributeExists("id", "updateRequest"))
                .andExpect(view().name("user-update-names"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testUpdateNamesUSER() throws Exception {
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
    public void testForbiddenUpdateNamesUSER() throws Exception {
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
    public void testDeleteUSER() throws Exception {
        long id = 5L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/auth/login"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testForbiddenDeleteUSER() throws Exception {
        long id = 4L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
