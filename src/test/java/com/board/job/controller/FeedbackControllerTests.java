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
    public void testInjectedComponents() {
        assertNotNull(mvc);
        assertNotNull(feedbackService);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void testGetAllCandidateMessengerFeedbacksCandidate() throws Exception {
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
    public void testForbiddenGetAllCandidateMessengerFeedbacksCandidate() throws Exception {
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
    public void testGetAllEmployerMessengerFeedbacksEmployer() throws Exception {
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
    public void testForbiddenGetAllEmployerMessengerFeedbacksAdmin() throws Exception {
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
    public void testCreateByCandidateAdmin() throws Exception {
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
    public void testForbiddenCreateByCandidateAdmin() throws Exception {
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
    public void testNotFoundCreateByCandidateAdmin() throws Exception {
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
    public void testCreateByEmployerEmployer() throws Exception {
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
    public void testForbiddenCreateByEmployerEmployer() throws Exception {
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
    public void testNotFoundCreateByEmployerEmployer() throws Exception {
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
    public void testGetUpdateFormByCandidateAdmin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = feedbackService.getAllVacancyMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}/edit",
                        ownerId, candidateId, messengerId, id)
                )
                .andExpect(view().name("candidates/feedback-update"))
                .andExpect(model().attributeExists("owner", "feedback"));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "EMPLOYER", "CANDIDATE"})
    public void testUpdateByCandidateAdmin() throws Exception {
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
    public void testForbiddenUpdateByCandidateAdmin() throws Exception {
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
    public void testNotFoundUpdateByCandidateAdmin() throws Exception {
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
    public void testGetUpdateFormByEmployerAdmin() throws Exception {
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
    public void testUpdateByEmployerEmployer() throws Exception {
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
    public void testForbiddenUpdateByEmployerEmployer() throws Exception {
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
    public void testNotFoundUpdateByEmployerEmployer() throws Exception {
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
    public void testDeleteByCandidateCandidate() throws Exception {
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
    public void testForbiddenDeleteByCandidateCandidate() throws Exception {
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
    public void testNotFoundDeleteByCandidateCandidate() throws Exception {
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
    public void testDeleteEmployer() throws Exception {
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
    public void testForbiddenDeleteByEmployerEmployer() throws Exception {
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
    public void testNotFoundDeleteByEmployerEmployer() throws Exception {
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
