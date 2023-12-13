package com.board.job.controller;

import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.sample.Category;
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
public class VacancyControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";

    private final MockMvc mvc;

    @Autowired
    public VacancyControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void testInjectedComponents() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetVacanciesAdmin() throws Exception {
        long ownerId = 4L;

        mvc.perform(get(BASIC_URL + "/vacancies", ownerId)
                        .param("sort_by", "postedAt")
                        .param("sort_order", "desc")
                        .param("page", "0")
                        .param("searchText", "Java")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page", "searchText", "sort_order", "owner", "sort_by",
                        "dateFormatter", "vacancies"))
                .andExpect(view().name("candidates/vacancies-list"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void testGetVacanciesCandidate() throws Exception {
        long ownerId = 3L;

        mvc.perform(get(BASIC_URL + "/vacancies", ownerId)
                        .param("sort_by", "postedAt")
                        .param("sort_order", "desc")
                        .param("page", "0")
                        .param("searchText", "")
                )
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page", "searchText", "sort_order", "owner", "sort_by",
                        "dateFormatter", "vacancies"))
                .andExpect(view().name("candidates/vacancies-list"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenGetAllEmployer() throws Exception {
        long ownerId = 2L;
        mvc.perform(get(BASIC_URL + "/vacancies", ownerId)
                        .param("sort_by", "postedAt")
                        .param("sort_order", "desc")
                        .param("page", "0")
                        .param("searchText", "")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetByIdAdmin() throws Exception {
        long ownerId = 1L;
        long id = 1L;

        mvc.perform(get(BASIC_URL + "/vacancies/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "vacancy", "messenger"))
                .andExpect(view().name("candidates/vacancy-get"));

    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void testGetByIdCandidate() throws Exception {
        long ownerId = 5L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/vacancies/{id}", ownerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "vacancy", "messenger"))
                .andExpect(view().name("candidates/vacancy-get"));

    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testNotFoundGetByIdAdmin() throws Exception {
        long ownerId = 1L;
        long id = 10000000L;

        mvc.perform(get(BASIC_URL + "/vacancies/{id}", ownerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());

    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetAllEmployerVacanciesAdmin() throws Exception {
        long ownerId = 1L;
        long employerId = 2L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies",
                        ownerId, employerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "vacancies", "dateFormatter"))
                .andExpect(view().name("employers/company-vacancies-list"));
    }
    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testGetAllForSelectEmployer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long candidateId = 2L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/vacancies/candidate/{candidate-id}",
                        ownerId, employerId, candidateId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vacancies", "dateFormatter", "candidateId"))
                .andExpect(view().name("employers/vacancies-for-select"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetEmployerVacancyAdmin() throws Exception {
        long ownerId = 1L;
        long employerId = 2L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                        ownerId, employerId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vacancy"))
                .andExpect(view().name("employers/vacancy-get"));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testForbiddenGetEmployerVacancyEmployer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long id = 1L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                        ownerId, employerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());

    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testNotFoundGetByEmployerVacancyEmployer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long id = 10000000L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                        ownerId, employerId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());

    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testGetCreateForm() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/create",
                        ownerId, employerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vacancy"))
                .andExpect(view().name("employers/vacancy-create"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testCreateAdmin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies", ownerId, employerId)
                        .param("lookingFor", "C++ Developer")
                        .param("domain", "Dating")
                        .param("detailDescription", "Descr")
                        .param("category", "C++")
                        .param("workMode", "Only Remote")
                        .param("country", "Poland")
                        .param("city", "Krakow")
                        .param("salaryFrom", "1000")
                        .param("salaryTo", "1500")
                        .param("workExperience", "1")
                        .param("englishLevel", "Intermediate")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies", ownerId, employerId)));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testBadRequestCreateEmployer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies", ownerId, employerId)
                        .param("lookingFor", "C++ Developer")
                        .param("domain", "MedTech")
                        .param("salaryTo", "1500")
                        .param("workExperience", "1")
                        .param("englishLevel", "Pre-Intermediate")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenCreateCandidate() throws Exception {
        long ownerId = 5L;
        long employerId = 3L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies", ownerId, employerId)
                        .param("lookingFor", "C++ Developer")
                        .param("domain", "MedTech")
                        .param("detailDescription", "Descr")
                        .param("category", "C++")
                        .param("workMode", "Remote")
                        .param("country", "Poland")
                        .param("city", "Krakow")
                        .param("salaryFrom", "1000")
                        .param("salaryTo", "1500")
                        .param("workExperience", "1")
                        .param("englishLevel", "Pre-Intermediate")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testUpdateEmployer() throws Exception {
        long ownerID = 6L;
        long employerId = 3L;
        long id = 6L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}/update", ownerID, employerId, id)
                        .param("lookingFor", "C++ Developer")
                        .param("domain", "Dating")
                        .param("detailDescription", "Descr")
                        .param("category", "C++")
                        .param("workMode", "Hybrid")
                        .param("country", "Poland")
                        .param("city", "Krakow")
                        .param("salaryFrom", "1000")
                        .param("salaryTo", "1500")
                        .param("workExperience", "1")
                        .param("englishLevel", "Intermediate")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies/%s",
                        ownerID, employerId, id)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testForbiddenUpdateADMIN() throws Exception {
        long ownerID = 1L;
        long employerId = 1L;
        long id = 4L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}/update", ownerID, employerId, id)
                        .param("lookingFor", "C++ Developer")
                        .param("domain", "MedTech")
                        .param("detailDescription", "Descr")
                        .param("category", "C++")
                        .param("workMode", "Remote")
                        .param("country", "Poland")
                        .param("city", "Krakow")
                        .param("salaryFrom", "1000")
                        .param("salaryTo", "1500")
                        .param("workExperience", "1")
                        .param("englishLevel", "Pre-Intermediate")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testBadRequestUpdateEmployer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long id = 8L;
        VacancyRequest vacancyRequest = VacancyRequest.builder().build();
        vacancyRequest.setCategory(Category.JAVA.getValue());
        vacancyRequest.setCity("city");

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}/update", ownerId, employerId, id)
                        .param("lookingFor", "C++ Developer")
                        .param("domain", "MedTech")
                        .param("detailDescription", "Descr")
                        .param("category", "C++")
                        .param("workMode", "Remote")
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void testDeleteEmployer() throws Exception {
        long ownerID = 6L;
        long employerId = 3L;
        long id = 7L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}/delete", ownerID, employerId, id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies", ownerID, employerId)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testForbiddenDeleteAdmin() throws Exception {
        long ownerID = 1L;
        long candidateID = 1L;
        long id = 5L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}/delete", ownerID, candidateID, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
