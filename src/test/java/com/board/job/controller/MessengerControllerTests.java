package com.board.job.controller;

import com.board.job.model.dto.messenger.CutMessengerResponse;
import com.board.job.model.entity.Messenger;
import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.MessengerService;
import com.board.job.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
public class MessengerControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";

    private final MockMvc mvc;
    private final UserService userService;

    private final MessengerMapper mapper;
    private final MessengerService messengerService;

    private String adminToken;
    private String employerToken;
    private String candidateToken;

    @Autowired
    public MessengerControllerTests(MockMvc mvc, UserService userService, MessengerMapper mapper,
                                    MessengerService messengerService) {
        this.mvc = mvc;
        this.userService = userService;
        this.mapper = mapper;
        this.messengerService = messengerService;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        employerToken = getUserToken(mvc, "violet@mail.co", "6666");
        candidateToken = getUserToken(mvc, "helen@mail.co", "5555");
    }

    @Test
    public void test_Injected_Components() {
        Assertions.assertNotNull(mvc);
        Assertions.assertNotNull(userService);
        Assertions.assertNotNull(mapper);
        Assertions.assertNotNull(messengerService);
    }

    @Test
    public void test_GetAllCandidateMessengers_Candidate() throws Exception {
        long candidateId = 4L;
        List<CutMessengerResponse> expected = messengerService.getAllByCandidateId(candidateId)
                .stream()
                .map(mapper::getCutMessengerResponseFromMessenger)
                .toList();

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers", 5L, candidateId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_NotFound_GetAllCandidateMessengers_Candidate() throws Exception {
        long candidateId = 0L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers", 5L, candidateId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"CandidateProfile not found\"")));
    }

    @Test
    public void test_Forbidden_GetAllCandidateMessengers_Candidate() throws Exception {
        long candidateId = 3L;

        mvc.perform(get(BASIC_URL + "/candidate/{candidate-id}/messengers", 5L, candidateId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_GetAllVacancyMessengers_Employer() throws Exception {
        long employerId = 3L;
        long vacancyId = 7L;
        List<CutMessengerResponse> expected = messengerService.getAllByVacancyId(vacancyId)
                .stream()
                .map(mapper::getCutMessengerResponseFromMessenger)
                .toList();

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers",
                        6L, employerId, vacancyId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_NotFound_GetAllVacancyMessengers_Admin() throws Exception {
        long employerId = 0L;
        long vacancyId = 0L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers",
                        1L, employerId, vacancyId)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Vacancy not found\"")));
    }

    @Test
    public void test_Forbidden_GetAllVacancyMessengers_Employer() throws Exception {
        long employerId = 3L;
        long vacancyId = 2L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers",
                        5L, employerId, vacancyId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Create_Admin() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 7L;
        Messenger messenger = messengerService.create(vacancyId,
                userService.readByEmail("admin@mail.co").getCandidateProfile().getId());

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers",
                        ownerId, employerId, vacancyId)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals(
                                String.format("Messenger between %s and candidate %s successfully created",
                                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                                        messenger.getCandidate().getOwner().getName()),
                                result.getResponse().getContentAsString()
                        )
                );
    }

    @Test
    public void test_Forbidden_Create_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 7L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers",
                        ownerId, employerId, vacancyId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED))
                );
    }

    @Test
    public void test_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 5L;
        long candidateId = 4L;
        long messengerId = 4L;

        Messenger messenger = messengerService.readById(messengerId);

        mvc.perform(delete(BASIC_URL + "/candidate/{candidate-id}/messengers/{id}", ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(
                                String.format("Messenger between %s and candidate %s successfully deleted",
                                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                                        messenger.getCandidate().getOwner().getName()),
                                result.getResponse().getContentAsString()
                        )
                );
    }

    @Test
    public void test_Forbidden_DeleteByCandidate_Candidate() throws Exception {
        long ownerId = 5L;
        long candidateId = 4L;
        long messengerId = 3L;

        mvc.perform(delete(BASIC_URL + "/candidate/{candidate-id}/messengers/{id}", ownerId, candidateId, messengerId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED))
                );
    }

    @Test
    public void test_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 7L;
        long messengerId = 2L;

        Messenger messenger = messengerService.readById(messengerId);

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(
                                String.format("Messenger between %s and candidate %s successfully deleted",
                                        messenger.getVacancy().getEmployerProfile().getCompanyName(),
                                        messenger.getCandidate().getOwner().getName()),
                                result.getResponse().getContentAsString()
                        )
                );
    }

    @Test
    public void test_Forbidden_DeleteByEmployer_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long vacancyId = 6L;
        long messengerId = 4L;

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{vacancy-id}/messengers/{id}",
                        ownerId, employerId, vacancyId, messengerId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED))
                );
    }
}
