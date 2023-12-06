package com.board.job.controller.employer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EmployerProfileControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}/employer-profiles";

    private final MockMvc mvc;

    @Autowired
    public EmployerProfileControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetById_User() throws Exception {
        long ownerId = 2L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "employerProfile"))
                .andExpect(view().name("employers/employer-profile-get"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetById_Admin() throws Exception {
        long ownerId = 6L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "employerProfile"))
                .andExpect(view().name("employers/employer-profile-get"));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_GetById_User() throws Exception {
        long ownerId = 6L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_NotFound_GetById_User() throws Exception {
        long ownerId = 6L;
        long id = 100L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_getCreateRequest_Admin() throws Exception {
        long ownerId = 1L;

        mvc.perform(get(BASIC_URL + "/create", ownerId))
                .andExpect(model().attributeExists("owner", "employerProfileRequest"))
                .andExpect(view().name("employers/employer-profile-create"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Create() throws Exception {
        long ownerID = 4L;

        mvc.perform(post(BASIC_URL, ownerID)
                        .param("employerName", "New name")
                        .param("positionInCompany", "HR")
                        .param("companyName", "Tumbor")
                        .param("telegram", "@tele")
                        .param("phone", "0688624050")
                        .param("linkedInProfile", "www.linkedin.co/fshjk")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/auth/login"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_Create_USER() throws Exception {
        long ownerID = 3L;
        mvc.perform(post(BASIC_URL, ownerID)
                        .param("employerName", "New name")
                        .param("positionInCompany", "HR")
                        .param("companyName", "Tumbor")
                        .param("telegram", "@tele")
                        .param("phone", "0688624050")
                        .param("linkedInProfile", "www.linkedin.co/fshjk")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Update_USER() throws Exception {
        long ownerID = 6L;
        long id = 3L;

        mvc.perform(post(BASIC_URL + "/{id}/update", ownerID, id)
                        .param("employerName", "New name")
                        .param("positionInCompany", "HR")
                        .param("companyName", "Tumbor")
                        .param("telegram", "@tele")
                        .param("phone", "0688624050")
                        .param("linkedInProfile", "www.linkedin.co/fshjk")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/employer-profiles/%d", ownerID, id)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(post(BASIC_URL + "/{id}/update", ownerID, candidateID)
                        .param("employerName", "New name")
                        .param("positionInCompany", "HR")
                        .param("companyName", "Tumbor")
                        .param("telegram", "@tele")
                        .param("phone", "0688624050")
                        .param("linkedInProfile", "www.linkedin.co/fshjk")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Delete_USER() throws Exception {
        long ownerID = 6L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/{id}/delete", ownerID, id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/employer-profiles/%d", ownerID, id)));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_Delete_USER() throws Exception {
        long ownerID = 2L;
        long candidateID = 3L;

        mvc.perform(get(BASIC_URL + "/{id}/delete", ownerID, candidateID))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
