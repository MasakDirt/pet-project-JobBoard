package com.board.job.controller;

import com.board.job.model.dto.messenger.FullMessengerResponse;
import com.board.job.model.mapper.FeedbackMapper;
import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.FeedbackService;
import com.board.job.service.MessengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class FeedbackControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";

    private final MockMvc mvc;
    private final FeedbackMapper mapper;
    private final MessengerMapper messengerMapper;
    private final FeedbackService feedbackService;
    private final MessengerService messengerService;

    private String adminToken;
    private String employerToken;
    private String candidateToken;

    @Autowired
    public FeedbackControllerTests(MockMvc mvc, FeedbackMapper mapper, MessengerMapper messengerMapper,
                                   FeedbackService feedbackService, MessengerService messengerService) {
        this.mvc = mvc;
        this.mapper = mapper;
        this.messengerMapper = messengerMapper;
        this.feedbackService = feedbackService;
        this.messengerService = messengerService;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        employerToken = getUserToken(mvc, "larry@mail.co", "2222");
        candidateToken = getUserToken(mvc, "nikole@mail.co", "3333");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mapper);
        assertNotNull(mapper);
        assertNotNull(mapper);
        assertNotNull(mapper);
    }

    @Test
    public void test_GetAllCandidateMessengerFeedbacks_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;

        FullMessengerResponse expected = messengerMapper.getFullMessengerResponseFromMessenger(messengerService.readById(messengerId),
                feedbackService.getAllMessengerFeedbacks(messengerId));

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetAllCandidateMessengerFeedbacks_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 1L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_GetAllEmployerMessengerFeedbacks_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;

        FullMessengerResponse expected = messengerMapper.getFullMessengerResponseFromMessenger(messengerService.readById(messengerId),
                feedbackService.getAllMessengerFeedbacks(messengerId));

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetAllEmployerMessengerFeedbacks_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;
        long vacancyId = 1L;
        long messengerId = 2L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("text", text)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string(String.format("Feedback with text %s successfully created", text)));
    }

    @Test
    public void test_Forbidden_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 4L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("text", text)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_CreateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 0L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks",
                        ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("text", text)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Messenger not found\"")));
    }

    @Test
    public void test_CreateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("text", text)
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals(String.format("Feedback with text %s successfully created", text),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_CreateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 2L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("text", text)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_CreateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 0L;
        String text = "Text";

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("text", text)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Messenger not found\"")));
    }

    @Test
    public void test_UpdateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = feedbackService.getAllMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(put(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}",
                        ownerId, candidateId, messengerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("text", text)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Feedback with text %s successfully updated", text)));
    }

    @Test
    public void test_Forbidden_UpdateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = feedbackService.getAllMessengerFeedbacks(2L)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(put(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}",
                        ownerId, candidateId, messengerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("text", text)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_UpdateByCandidate_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;
        long messengerId = 1L;
        String id = "";
        String text = "Updated";

        mvc.perform(put(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}",
                        ownerId, candidateId, messengerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("text", text)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_UpdateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = feedbackService.getAllMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(put(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("text", text)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(String.format("Feedback with text %s successfully updated", text),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_UpdateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 2L;
        String id = feedbackService.getAllMessengerFeedbacks(1L)
                .stream().findAny().get().getId();
        String text = "Updated";

        mvc.perform(put(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("text", text)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_UpdateByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = "";
        String text = "Updated";

        mvc.perform(put(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("text", text)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void test_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;
        String id = feedbackService.getAllMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();

        mvc.perform(delete(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}",
                        ownerId, candidateId, messengerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Feedback successfully deleted"));
    }

    @Test
    public void test_Forbidden_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;
        String id = feedbackService.getAllMessengerFeedbacks(1L)
                .stream().findAny().get().getId();

        mvc.perform(delete(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}",
                        ownerId, candidateId, messengerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long messengerId = 2L;
        String id = "notfound";

        mvc.perform(delete(BASIC_URL + "/candidate/{candidate-id}/messengers/{messengerId}/feedbacks/{id}",
                        ownerId, candidateId, messengerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Feedback not found\"")));
        ;
    }

    @Test
    public void test_Delete_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = feedbackService.getAllMessengerFeedbacks(messengerId)
                .stream().findAny().get().getId();

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Feedback successfully deleted"));
    }

    @Test
    public void test_Forbidden_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 2L;
        String id = feedbackService.getAllMessengerFeedbacks(1L)
                .stream().findAny().get().getId();

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long vacancyId = 5L;
        long messengerId = 4L;
        String id = "";

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{messenger-id}/feedbacks/{id}",
                        ownerId, employerId, vacancyId, messengerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isNotFound());
    }
}
