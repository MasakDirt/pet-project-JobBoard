package com.board.job.controller.candidate;

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
public class CandidateProfileControllerTests {
    private final static String BASIC_URl = "/api/users/{owner-id}/candidate-profiles";

    private final MockMvc mvc;

    @Autowired
    public CandidateProfileControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetAll_Admin() throws Exception {
        long ownerId = 1L;

        mvc.perform(get(BASIC_URl, ownerId)
                        .param("sort_by", "category")
                        .param("sort_order", "asc")
                        .param("page", "0")
                        .param("searchText", "")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page", "searchText", "sort_order", "sort_by", "owner", "candidates"))
                .andExpect(view().name("employers/candidates-list"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetAll_Employer() throws Exception {
        long ownerId = 2L;

        mvc.perform(get(BASIC_URl, ownerId)
                        .param("sort_by", "category")
                        .param("sort_order", "asc")
                        .param("page", "0")
                        .param("searchText", "")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page", "searchText", "sort_order", "sort_by", "owner", "candidates"))
                .andExpect(view().name("employers/candidates-list"));

    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetAll_Candidate() throws Exception {
        long ownerId = 3L;

        mvc.perform(get(BASIC_URl, ownerId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_GetById_Admin() throws Exception {
        long ownerId = 1L;
        long id = 2L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories", "eng_levels", "ukr_levels", "owner", "candidateProfileRequest"))
                .andExpect(view().name("candidates/candidate-profile-get"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetById_Employer() throws Exception {
        long ownerId = 2L;
        long id = 4L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories", "eng_levels", "ukr_levels", "owner", "candidateProfileRequest"))
                .andExpect(view().name("candidates/candidate-profile-get"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_GetById_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 2L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories", "eng_levels", "ukr_levels", "owner", "candidateProfileRequest"))
                .andExpect(view().name("candidates/candidate-profile-get"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetById_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 3L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetCandidateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long candidateId = 4L;

        mvc.perform(get("/api/users/{owner-id}/employer/{employer-id}/candidate-profiles/{candidate-id}", ownerId, employerId, candidateId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("categories", "eng_levels", "ukr_levels", "owner", "candidateProfileRequest"))
                .andExpect(view().name("employers/candidate-get"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetCandidateByEmployer_Candidate() throws Exception {
        long ownerId = 3L;
        long employerId = 2L;
        long candidateId = 2L;

        mvc.perform(get("/api/users/{owner-id}/employer/{employer-id}/candidate-profiles/{candidate-id}", ownerId, employerId, candidateId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_getCreateRequest_Admin() throws Exception {
        long ownerId = 1L;

        mvc.perform(get(BASIC_URl + "/create", ownerId))
                .andExpect(model().attributeExists("categories", "eng_levels", "ukr_levels", "owner", "candidateProfileRequest"))
                .andExpect(view().name("candidates/candidate-profile-create"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Create() throws Exception {
        long ownerId = 2L;

        mvc.perform(post(BASIC_URl, ownerId)
                        .param("position", "Java Jun")
                        .param("salaryExpectations", "2500")
                        .param("workExperience", "2")
                        .param("countryOfResidence", "Ukraine")
                        .param("cityOfResidence", "Kyiv")
                        .param("category", "Java")
                        .param("englishLevel", "Pre-Intermediate")
                        .param("ukrainianLevel", "Pre-Intermediate")
                        .param("experienceExplanation", "My experience")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/api/auth/login"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_InternalServerError_Create_Admin() throws Exception {
        long ownerId = 1L;

        mvc.perform(post(BASIC_URl, ownerId)
                        .param("position", "Python")
                        .param("salaryExpectations", "4400")
                        .param("workExperience", "6")
                        .param("countryOfResidence", "Ukraine")
                        .param("cityOfResidence", "Lviv")
                        .param("category", "Python")
                        .param("englishLevel", "Pre-Intermediate")
                        .param("ukrainianLevel", "Pre-Intermediate")
                        .param("experienceExplanation", "My experience")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_Create_Candidate() throws Exception {
        long ownerId = 1L;

        mvc.perform(post(BASIC_URl, ownerId)
                        .param("position", "C++")
                        .param("salaryExpectations", "500")
                        .param("workExperience", "0.5")
                        .param("countryOfResidence", "Ukraine")
                        .param("cityOfResidence", "Dnipro")
                        .param("category", "C++")
                        .param("englishLevel", "Intermediate")
                        .param("ukrainianLevel", "Intermediate")
                        .param("experienceExplanation", "My experience")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_BadRequest_Create_Employer() throws Exception {
        long ownerId = 2L;

        mvc.perform(post(BASIC_URl, ownerId)
                        .param("position", "C++")
                        .param("salaryExpectations", "500")
                        .param("workExperience", "0.5")
                        .param("countryOfResidence", "Ukraine")
                        .param("cityOfResidence", "Dnipro")
                        .param("category", "C++")
                        .param("englishLevel", "Intermediate")
                        .param("ukrainianLevel", "Intermediate")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Update_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 2L;

        mvc.perform(post(BASIC_URl + "/{id}/update", ownerId, id)
                        .param("position", "C++")
                        .param("salaryExpectations", "500")
                        .param("workExperience", "0.5")
                        .param("countryOfResidence", "Ukraine")
                        .param("cityOfResidence", "Dnipro")
                        .param("category", "C++")
                        .param("englishLevel", "Intermediate")
                        .param("ukrainianLevel", "Intermediate")
                        .param("experienceExplanation", "My exp")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/candidate-profiles/%d", ownerId, id)));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_Update_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 1L;

        mvc.perform(post(BASIC_URl + "/{id}/update", ownerId, id)
                        .param("position", "C++")
                        .param("salaryExpectations", "500")
                        .param("workExperience", "0.5")
                        .param("countryOfResidence", "Ukraine")
                        .param("cityOfResidence", "Dnipro")
                        .param("category", "C++")
                        .param("englishLevel", "Intermediate")
                        .param("ukrainianLevel", "Intermediate")
                        .param("experienceExplanation", "My exp")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Delete_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 2L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", ownerId, id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d", ownerId)));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_Delete_Admin() throws Exception {
        long ownerId = 3L;
        long id = 1L;

        mvc.perform(get(BASIC_URl + "/{id}/delete", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
