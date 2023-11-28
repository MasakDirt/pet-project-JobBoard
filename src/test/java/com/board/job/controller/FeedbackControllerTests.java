package com.board.job.controller;

import com.board.job.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.getErrorAttributes;
import static com.board.job.helper.HelperForTests.getErrorView;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class FeedbackControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";
    private final MockMvc mvc;
    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackControllerTests(MockMvc mvc, FeedbackService feedbackService) {
        this.mvc = mvc;
        this.feedbackService = feedbackService;
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(feedbackService);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_GetAllCandidateMessengerFeedbacks_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                )
                .andExpect(view().name("candidates/feedbacks-list"))
                .andExpect(model().attributeExists("owner", "dateFormatter", "messenger"));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_GetAllCandidateMessengerFeedbacks_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 1L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetAllEmployerMessengerFeedbacks_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                )
                .andExpect(view().name("employers/feedbacks-list"))
                .andExpect(model().attributeExists("owner", "dateFormatter", "messenger", "candidate"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_GetAllEmployerMessengerFeedbacks_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;
        long vacancyId = 1L;
        long messengerId = 2L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .param("text", text)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                        ownerId, candidateId, messengerId)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_Forbidden_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 4L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .param("text", text)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_NotFound_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 0L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .param("text", text)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_CreateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .param("text", text)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_CreateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 2L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .param("text", text)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_NotFound_CreateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 0L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .param("text", text)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_GetUpdateFormByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/edit",
                        ownerId, candidateId, messengerId, id)
                )
                .andExpect(view().name("candidates/feedback-update"))
                .andExpect(model().attributeExists("owner", "feedback"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_UpdateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/edit",
                        ownerId, candidateId, messengerId, id)
                        .param("text", text)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                        ownerId, candidateId, messengerId)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_Forbidden_UpdateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(2L)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/edit",
                        ownerId, candidateId, messengerId, id)
                        .param("text", text)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void test_NotFound_UpdateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = "";
        String text = "Updated";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/edit",
                        ownerId, candidateId, messengerId, id)
                        .param("text", text)
                )
                .andExpect(status().is4xxClientError());

    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_GetUpdateFormByEmployer_Admin() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/edit",
                        ownerId, employerId, vacancyId, messengerId, id)
                )
                .andExpect(view().name("employers/feedback-update"))
                .andExpect(model().attributeExists("owner", "feedback", "employerId", "vacancyId", "messengerId"));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_UpdateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/edit",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .param("text", text)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_UpdateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 2L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(1L)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/edit",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .param("text", text)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_NotFound_UpdateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = "";
        String text = "Updated";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/edit",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .param("text", text)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().filter(feedback -> feedback.getOwnerId() == ownerId).findAny().get().getId();

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/delete",
                        ownerId, candidateId, messengerId, id)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%s/candidate/%s/messengers/%s/feedbacks",
                        ownerId, candidateId, messengerId)));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_Forbidden_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(1L)
                .stream().findAny().get().getId();

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/delete",
                        ownerId, candidateId, messengerId, id)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_NotFound_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;
        String id = "notfound";

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/delete",
                        ownerId, candidateId, messengerId, id)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Delete_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/delete",
                        ownerId, employerId, vacancyId, messengerId, id)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%s/employer-profile/%s/vacancies/%s/messengers/%s/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 2L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(1L)
                .stream().findAny().get().getId();

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/delete",
                        ownerId, employerId, vacancyId, messengerId, id)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_NotFound_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = "";

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}/delete",
                        ownerId, employerId, vacancyId, messengerId, id)
                )
                .andExpect(status().is4xxClientError());
    }
}
