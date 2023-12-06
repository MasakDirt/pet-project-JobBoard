package com.board.job.controller;

import com.board.job.service.VacancyService;
import org.junit.jupiter.api.Assertions;
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
public class MessengerControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";

    private final MockMvc mvc;
    private final VacancyService vacancyService;

    @Autowired
    public MessengerControllerTests(MockMvc mvc, VacancyService vacancyService) {
        this.mvc = mvc;
        this.vacancyService = vacancyService;
    }

    @Test
    public void test_Injected_Components() {
        Assertions.assertNotNull(mvc);
        Assertions.assertNotNull(vacancyService);
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_GetAllCandidateMessengers_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers", ownerId, candidateId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "dateFormatter", "messengers"))
                .andExpect(view().name("messengers-list"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_NotFound_GetAllCandidateMessengers_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 0L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers", ownerId, candidateId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetAllCandidateMessengers_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 1L;


        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers", ownerId, candidateId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetAllVacancyMessengers_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 7L;

        mvc.perform(get(BASIC_URL + "/vacancies/{vacancy-id}/employer-profile/{employer-id}/messengers",
                        ownerId, vacancyId, employerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "dateFormatter", "messengers", "vacancyId",
                        "employerId"))
                .andExpect(view().name("employers/employer-messengers-list"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_NotFound_GetAllVacancyMessengers_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 0L;
        long vacancyId = 0L;
        long candidateId = 2L;

        mvc.perform(get(BASIC_URL + "/vacancies/{vacancy-id}/candidate/{candidate-id}/" +
                                "employer-profile/{employer-id}/messengers",
                        ownerId, vacancyId, candidateId, employerId))
                .andExpect(status().isOk())
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_GetAllVacancyMessengers_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 2L;
        long candidateId = 2L;

        mvc.perform(get(BASIC_URL + "/vacancies/{vacancy-id}/candidate/{candidate-id}/" +
                                "employer-profile/{employer-id}/messengers",
                        ownerId, vacancyId, candidateId, employerId))
                .andExpect(status().isOk())
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long vacancyId = 5L;
        long candidateId = 1L;

        mvc.perform(post(BASIC_URL + "/vacancies/{vacancy-id}/candidate/{candidate-id}/messengers",
                        ownerId, vacancyId, candidateId)
                        .param("text", "text")
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                        ownerId, candidateId, 7L)));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_CreateByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long vacancyId = 8L;
        long candidateId = 1L;

        mvc.perform(post(BASIC_URL + "/vacancies/{vacancy-id}/candidate/{candidate-id}/messengers",
                        ownerId, vacancyId, candidateId))
                .andExpect(status().isOk())
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void test_CreateByEmployer_Admin() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 7L;
        long candidateId = 3L;

        mvc.perform(post(BASIC_URL + "/vacancies/{vacancy-id}/candidate/{candidate-id}/" +
                                "employer-profile/{employer-id}/messengers",
                        ownerId, vacancyId, candidateId, employerId))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                        ownerId, vacancyService.readById(vacancyId).getEmployerProfile().getId(), vacancyId, 8L)));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 5L;
        long candidateId = 4L;
        long messengerId = 4L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{id}/delete",
                        ownerId, candidateId, messengerId))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/candidate/%s/messengers",
                        ownerId, candidateId)));
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 5L;
        long candidateId = 4L;
        long messengerId = 3L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{id}/delete",
                        ownerId, candidateId, messengerId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 7L;
        long messengerId = 2L;

        mvc.perform(get(BASIC_URL + "/vacancies/{vacancy-id}/employer-profile/{employer-id}/messengers/{id}/delete",
                        ownerId, vacancyId, employerId, messengerId))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%s/vacancies/%s/employer-profile/%s/messengers",
                        ownerId, vacancyId, employerId)));
    }

    @Test
    @WithMockUser(username = "violet@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 6L;
        long messengerId = 4L;

        mvc.perform(get(BASIC_URL + "/vacancies/{vacancy-id}/employer-profile/{employer-id}/messengers/{id}/delete",
                        ownerId, vacancyId, employerId, messengerId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
