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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EmployerCompanyControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}/employer-companies";

    private final MockMvc mvc;

    @Autowired
    public EmployerCompanyControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void testInjectedComponents() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testGetByIdUser() throws Exception {
        long ownerId = 2L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "employerCompany"))
                .andExpect(view().name("employers/employer-company-get"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetByIdAdmin() throws Exception {
        long ownerId = 6L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "employerCompany"))
                .andExpect(view().name("employers/employer-company-get"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenGetByIdUser() throws Exception {
        long ownerId = 6L;
        long id = 1L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testNotFoundGetByIdUser() throws Exception {
        long ownerId = 6L;
        long id = 10L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetCreateRequestAdmin() throws Exception {
        long ownerId = 1L;
        mvc.perform(get(BASIC_URL + "/create", ownerId))
                .andExpect(model().attributeExists("owner", "webSite", "aboutCompany"))
                .andExpect(view().name("employers/employer-company-create"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void testCreate() throws Exception {
        long ownerID = 3L;

        mvc.perform(post(BASIC_URL, ownerID)
                        .param("aboutCompany", "About")
                        .param("webSite", "web")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/employer-companies/%d", ownerID, 4L)));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenCreateUSER() throws Exception {
        long ownerID = 1L;

        mvc.perform(post(BASIC_URL, ownerID)
                        .param("about_company", "About")
                        .param("web_site", "web")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testUpdateUSER() throws Exception {
        long ownerID = 6L;
        long id = 3L;

        mvc.perform(post(BASIC_URL + "/{id}/update", ownerID, id)
                        .param("about_company", "Updated")
                        .param("web_site", "new")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/employer-companies/%d", ownerID, id)));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenUpdateADMIN() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(post(BASIC_URL + "/{id}/update", ownerID, candidateID)
                        .param("about_company", "Updated")
                        .param("web_site", "new")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testDeleteUSER() throws Exception {
        long ownerID = 6L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/{id}/delete", ownerID, id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d", ownerID)));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenDeleteUSER() throws Exception {
        long ownerID = 2L;
        long candidateID = 3L;

        mvc.perform(get(BASIC_URL + "/{id}/delete", ownerID, candidateID))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
