package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_contact.CandidateContactRequest;
import com.board.job.model.mapper.candidate.CandidateContactMapper;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.board.job.helper.HelperForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CandidateContactControllerTests {
    private final static String BASIC_URl = "/api/users/{owner-id}/candidate-contacts";

    private final MockMvc mvc;
    private final CandidateContactService candidateContactService;
    private final CandidateContactMapper mapper;
    private final UserService userService;

    private String adminToken;
    private String nikoleToken;
    private String donaldToken;

    @Autowired
    public CandidateContactControllerTests(MockMvc mvc, CandidateContactService candidateContactService,
                                           CandidateContactMapper mapper, UserService userService) {
        this.mvc = mvc;
        this.candidateContactService = candidateContactService;
        this.mapper = mapper;
        this.userService = userService;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        nikoleToken = getUserToken(mvc, "nikole@mail.co", "3333");
        donaldToken = getUserToken(mvc, "donald@mail.co", "4444");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(candidateContactService);
        assertNotNull(mapper);
        assertNotNull(userService);
    }

    @Test
    public void test_getCandidateById_ADMIN() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        String expected = asJsonString(
                mapper.getCandidateContactResponseFromCandidateContact(candidateContactService.readById(candidateId)));

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, candidateId)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Valid_getCandidateById_USER() throws Exception {
        long ownerId = 4L;
        long candidateId = 3L;
        String expected = asJsonString(
                mapper.getCandidateContactResponseFromCandidateContact(candidateContactService.readById(candidateId)));

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, candidateId)
                        .header("Authorization", "Bearer " + donaldToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(expected, result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_getCandidateById_USER() throws Exception {
        long ownerId = 1L;
        long candidateId = 1L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, candidateId)
                        .header("Authorization", "Bearer " + nikoleToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_getCandidateById_ADMIN() throws Exception {
        mvc.perform(get(BASIC_URl + "/{id}", 100L, 10L)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Candidate not found\"")));
    }

    @Test
    public void test_Create() throws Exception {
        String userToken = registerUserAndGetHisToken(mvc);
        long ownerID = userService.readByEmail("maks@mail.co").getId();

        mvc.perform(post(BASIC_URl, ownerID)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getCandidate()))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("Your contacts successfully recorded.",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_BadRequest_Create_USER() throws Exception {
        long ownerID = 3L;
        CandidateContactRequest candidateContactRequest = CandidateContactRequest.builder().build();
        candidateContactRequest.setCandidateName("New");
        candidateContactRequest.setEmail("email@mail.co");

        mvc.perform(post(BASIC_URl, ownerID)
                        .header("Authorization", "Bearer " + nikoleToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(candidateContactRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"BAD_REQUEST\"")));
    }

    @Test
    public void test_Forbidden_Create_USER() throws Exception {
        long ownerID = 2L;

        mvc.perform(post(BASIC_URl, ownerID)
                        .header("Authorization", "Bearer " + nikoleToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getCandidate()))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Update_USER() throws Exception {
        long ownerID = 4L;
        long candidateID = 3L;

        mvc.perform(put(BASIC_URl + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + donaldToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getCandidate()))
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Your contacts successfully updated.",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(put(BASIC_URl + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getCandidate()))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                                .contains(ACCESS_DENIED),
                        "Here must be access denied status because user is not owner of candidate"));
    }

    @Test
    public void test_Delete_USER() throws Exception {
        long ownerID = 3L;
        long candidateID = 2L;
        String name = candidateContactService.readById(candidateID).getCandidateName();

        mvc.perform(delete(BASIC_URl + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + nikoleToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(String.format("Candidate contact with name %s successfully deleted", name),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Delete_USER() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(delete(BASIC_URl + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + donaldToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                                .contains(ACCESS_DENIED),
                        "Here must be access denied status because user is not sames."));
    }

    private CandidateContactRequest getCandidate() {
        CandidateContactRequest candidateContactRequest = CandidateContactRequest.builder().build();
        candidateContactRequest.setCandidateName("New");
        candidateContactRequest.setEmail("email@mail.co");
        candidateContactRequest.setPhone("00489344355");
        candidateContactRequest.setGithubUrl("www.github.co/23424");
        candidateContactRequest.setTelegram("@tele");
        candidateContactRequest.setPortfolioUrl("www.portfolio.co/eorRIEOFk");
        candidateContactRequest.setLinkedInProfile("www.linkedin.co/OKFOkdld");

        return candidateContactRequest;
    }
}
