package com.board.job.controller.candidate;

import com.board.job.model.dto.candidate_profile.CandidateProfileRequest;
import com.board.job.model.dto.candidate_profile.CutCandidateProfileResponse;
import com.board.job.model.dto.candidate_profile.FullCandidateProfileResponse;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.mapper.candidate.CandidateProfileMapper;
import com.board.job.service.candidate.CandidateProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

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
    private final CandidateProfileService candidateProfileService;
    private final CandidateProfileMapper mapper;

    private String adminToken;
    private String employerToken;
    private String candidateToken;

    @Autowired
    public CandidateProfileControllerTests(MockMvc mvc, CandidateProfileService candidateProfileService,
                                           CandidateProfileMapper mapper) {
        this.mvc = mvc;
        this.candidateProfileService = candidateProfileService;
        this.mapper = mapper;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        employerToken = getUserToken(mvc, "larry@mail.co", "2222");
        candidateToken = getUserToken(mvc, "nikole@mail.co", "3333");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(candidateProfileService);
        assertNotNull(mapper);
    }

    @Test
    public void test_GetAll_Admin() throws Exception {
        long ownerId = 1L;

        mvc.perform(get(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(getCandidateFour()) &&

                        result.getResponse().getContentAsString()
                                .contains(getCandidateThree())));
    }

    @Test
    public void test_GetAll_Employer() throws Exception {
        long ownerId = 2L;

        mvc.perform(get(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(getCandidateFour()) &&

                        result.getResponse().getContentAsString()
                                .contains(getCandidateThree())));
    }

    @Test
    public void test_Forbidden_GetAll_Candidate() throws Exception {
        long ownerId = 3L;

        mvc.perform(get(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_GetSortedVacancies_Admin() throws Exception {
        long ownerId = 1L;

        List<CutCandidateProfileResponse> expected = getListOfCandidateResponses(3L, 4L, 2L, 1L);

        mvc.perform(get(BASIC_URl + "/sorted", ownerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("sort_by", "category")
                        .param("sort_order", "asc")
                        .param("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetSortedVacancies_Employer() throws Exception {
        long ownerId = 2L;

        List<CutCandidateProfileResponse> expected = getListOfCandidateResponses(3L, 1L, 2L, 4L);

        mvc.perform(get(BASIC_URl + "/sorted", ownerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("sort_by", "workExperience", "englishLevel")
                        .param("sort_order", "desc")
                        .param("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetSortedVacancies_Candidate() throws Exception {
        long ownerId = 3L;

        mvc.perform(get(BASIC_URl + "/sorted", ownerId)
                        .header("Authorization", "Bearer " + candidateToken)
                        .param("sort_by", "workExperience", "englishLevel")
                        .param("sort_order", "desc")
                        .param("page", "0")
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_GetById_Admin() throws Exception {
        long ownerId = 1L;
        long id = 2L;

        FullCandidateProfileResponse expected = mapper.getCandidateProfileResponseFromCandidateProfile
                (candidateProfileService.readById(id));

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetById_Employer() throws Exception {
        long ownerId = 2L;
        long id = 4L;

        FullCandidateProfileResponse expected = mapper.getCandidateProfileResponseFromCandidateProfile
                (candidateProfileService.readById(id));

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetById_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 2L;

        FullCandidateProfileResponse expected = mapper.getCandidateProfileResponseFromCandidateProfile
                (candidateProfileService.readById(id));

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetById_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 3L;

        mvc.perform(get(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Create_User() throws Exception {
        String token = registerUserAndGetHisToken(mvc);
        long ownerId = 7L;
        String expected = asJsonString(create("Java Jun", 700, 0.7,
                "Ukraine", "Lviv", Category.JAVA, LanguageLevel.INTERMEDIATE,
                LanguageLevel.ADVANCED_FLUENT, "Experience"));

        List<CandidateProfile> before = candidateProfileService.getAll();

        mvc.perform(post(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expected)
                )
                .andExpect(status().isCreated())
                .andExpect(result ->
                        assertEquals("Candidate profile for user Maks Korniev successfully created",
                                result.getResponse().getContentAsString()));

        List<CandidateProfile> after = candidateProfileService.getAll();

        assertTrue(before.size() < after.size());
    }

    @Test
    public void test_InternalServerError_Create_Admin() throws Exception {
        long ownerId = 1L;
        String expected = asJsonString(create("Java Jun", 700, 0.7,
                "Ukraine", "Lviv", Category.JAVA, LanguageLevel.INTERMEDIATE,
                LanguageLevel.ADVANCED_FLUENT, "Experience"));

        mvc.perform(post(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expected)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"INTERNAL_SERVER_ERROR\",\"message\":\"could not execute statement")));
    }

    @Test
    public void test_Forbidden_Create_Employer() throws Exception {
        long ownerId = 1L;

        String expected = asJsonString(create("PHP developer", 2000, 2,
                "Poland", "Krakow", Category.PHP, LanguageLevel.UPPER_INTERMEDIATE,
                LanguageLevel.ADVANCED_FLUENT, "Experience"));

        mvc.perform(post(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expected)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_BadRequest_Create_Employer() throws Exception {
        long ownerId = 7L;

        String expected = asJsonString(create("Kotlin developer", 2000, 4,
                "Ukraine", "Odessa", Category.KOTLIN, LanguageLevel.UPPER_INTERMEDIATE,
                LanguageLevel.ADVANCED_FLUENT, null));

        mvc.perform(post(BASIC_URl, ownerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expected)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"BAD_REQUEST\",\"message\":\"Tell your experience here, if you have no, write about your study\"")));
    }

    @Test
    public void test_Update_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 2L;
        CandidateProfileRequest request = create("PHP developer", 2000, 2,
                "Poland", "Krakow", Category.PHP, LanguageLevel.UPPER_INTERMEDIATE,
                LanguageLevel.ADVANCED_FLUENT, "Experience");

        CandidateProfile profile = candidateProfileService.readById(id);
        String unexpected = asJsonString(profile);

        mvc.perform(put(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(String.format(
                                        "Candidate profile for user %s successfully updated", profile.getOwner().getName()
                                ),
                                result.getResponse().getContentAsString()));

        String actual = asJsonString(candidateProfileService.readById(id));

        assertNotEquals(unexpected, actual);
    }

    @Test
    public void test_Forbidden_Update_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 1L;
        CandidateProfileRequest request = create("PHP developer", 2000, 2,
                "Poland", "Krakow", Category.PHP, LanguageLevel.UPPER_INTERMEDIATE,
                LanguageLevel.ADVANCED_FLUENT, "Experience");

        mvc.perform(put(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Delete_Candidate() throws Exception {
        long ownerId = 3L;
        long id = 2L;
        CandidateProfile profile = candidateProfileService.readById(id);

        mvc.perform(delete(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertEquals(String.format(
                                        "Candidate profile for user %s successfully deleted", profile.getOwner().getName()
                                ),
                                result.getResponse().getContentAsString())
                );
    }

    @Test
    public void test_Forbidden_Delete_Admin() throws Exception {
        long ownerId = 3L;
        long id = 1L;

        mvc.perform(delete(BASIC_URl + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED)));
    }

    private String getCandidateFour() {
        return "{\"id\":4,\"position\":\"Trainee Java Developer\",\"category\":\"JAVA\",\"salary_expectations\":300," +
                "\"work_experience\":0.0,\"country_of_residence\":\"Poland\",\"english_level\":\"BEGINNER_ELEMENTARY\"}";
    }

    private String getCandidateThree() {
        return "{\"id\":3,\"position\":\"Senior C++ Developer\",\"category\":\"C_PLUS_PLUS\",\"salary_expectations\"" +
                ":5000,\"work_experience\":6.0,\"country_of_residence\":\"Ukraine\",\"english_level\":" +
                "\"UPPER_INTERMEDIATE\"}";
    }

    private List<CutCandidateProfileResponse> getListOfCandidateResponses(long id1, long id2, long id3, long id4) {
        return Stream.of(
                        candidateProfileService.readById(id1), candidateProfileService.readById(id2),
                        candidateProfileService.readById(id3), candidateProfileService.readById(id4)
                )
                .map(mapper::getCutCandidateProfileResponseFromCandidateProfile)
                .toList();
    }

    private CandidateProfileRequest create(
            String position, int salaryExpectations, double workExperience, String countryOfResidence, String cityOfResidence,
            Category category, LanguageLevel englishLevel, LanguageLevel ukrainianLevel, String experienceExplanation
    ) {
        CandidateProfileRequest candidateProfileRequest = CandidateProfileRequest.builder().build();
        candidateProfileRequest.setPosition(position);
        candidateProfileRequest.setSalaryExpectations(salaryExpectations);
        candidateProfileRequest.setWorkExperience(workExperience);
        candidateProfileRequest.setCountryOfResidence(countryOfResidence);
        candidateProfileRequest.setCityOfResidence(cityOfResidence);
        candidateProfileRequest.setCategory(category);
        candidateProfileRequest.setEnglishLevel(englishLevel);
        candidateProfileRequest.setUkrainianLevel(ukrainianLevel);
        candidateProfileRequest.setExperienceExplanation(experienceExplanation);
        candidateProfileRequest.setAchievements("");

        return candidateProfileRequest;
    }
}
