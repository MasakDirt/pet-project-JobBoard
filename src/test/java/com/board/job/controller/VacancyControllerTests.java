package com.board.job.controller;

import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import com.board.job.model.mapper.VacancyMapper;
import com.board.job.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

import static com.board.job.helper.HelperForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class VacancyControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";

    private final MockMvc mvc;
    private final VacancyService vacancyService;
    private final VacancyMapper mapper;

    private String adminToken;
    private String employerToken;
    private String candidateToken;

    @Autowired
    public VacancyControllerTests(MockMvc mvc, VacancyService vacancyService, VacancyMapper mapper) {
        this.mvc = mvc;
        this.vacancyService = vacancyService;
        this.mapper = mapper;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        employerToken = getUserToken(mvc, "violet@mail.co", "6666");
        candidateToken = getUserToken(mvc, "helen@mail.co", "5555");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(vacancyService);
        assertNotNull(mapper);
    }

    @Test
    public void test_GetAll_Admin() throws Exception {
        long ownerId = 4L;
        List<CutVacancyResponse> expected = vacancyService.getAll()
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/vacancies", ownerId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetAll_Candidate() throws Exception {
        long ownerId = 3L;
        List<CutVacancyResponse> expected = vacancyService.getAll()
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/vacancies", ownerId)
                        .header("Authorization", "Bearer " + candidateToken))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetAll_Employer() throws Exception {
        long ownerId = 3L;

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/vacancies", ownerId)
                        .header("Authorization", "Bearer " + employerToken))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_getSortedVacancies_Admin() throws Exception {
        long ownerId = 1L;

        List<CutVacancyResponse> expected = getSortedVacancies(8L, 4L, 5L, 2L, 1L);

        mvc.perform(get(BASIC_URL + "/vacancies/sorted", ownerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("sort_by", "domain", "category")
                        .param("sort_order", "desc")
                        .param("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));

    }

    @Test
    public void test_Forbidden_getSortedVacancies_Employer() throws Exception {
        long ownerId = 6L;

        mvc.perform(get(BASIC_URL + "/vacancies/sorted", ownerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .param("sort_by", "domain", "category")
                        .param("sort_order", "desc")
                        .param("page", "0")
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));

    }

    @Test
    public void test_getSortedVacancies_Candidate() throws Exception {
        long ownerId = 5L;

        List<CutVacancyResponse> expected = getSortedVacancies(2L, 8L, 7L, 1L, 3L);

        mvc.perform(get(BASIC_URL + "/vacancies/sorted", ownerId)
                        .header("Authorization", "Bearer " + candidateToken)
                        .param("sort_by", "category")
                        .param("sort_order", "desc")
                        .param("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));

    }

    @Test
    public void test_getById_Admin() throws Exception {
        long ownerId = 1L;
        long id = 1L;
        FullVacancyResponse expected = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));

        mvc.perform(get(BASIC_URL + "/vacancies/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));

    }

    @Test
    public void test_getById_Candidate() throws Exception {
        long ownerId = 5L;
        long id = 3L;
        FullVacancyResponse expected = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));

        mvc.perform(get(BASIC_URL + "/vacancies/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));

    }

    @Test
    public void test_NotFound_getById_Admin() throws Exception {
        long ownerId = 1L;
        long id = 10000000L;

        mvc.perform(get(BASIC_URL + "/vacancies/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Vacancy not found\"")));

    }

    @Test
    public void test_getAllEmployerVacancies_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 2L;
        List<CutVacancyResponse> expected = vacancyService.getAllByEmployerProfileId(employerId)
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/employer-profile/{employer-id}/vacancies",
                                ownerId, employerId)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_getAllEmployerVacancies_Candidate() throws Exception {
        long ownerId = 5L;
        long employerId = 3L;
        List<CutVacancyResponse> expected = vacancyService.getAllByEmployerProfileId(employerId)
                .stream()
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/employer-profile/{employer-id}/vacancies",
                                ownerId, employerId)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_getEmployerVacancy_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 2L;
        long id = 3L;
        FullVacancyResponse expected = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                                ownerId, employerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_getEmployerVacancy_Candidate() throws Exception {
        long ownerId = 5L;
        long employerId = 3L;
        long id = 7L;
        FullVacancyResponse expected = mapper.getFullVacancyResponseFromVacancy(vacancyService.readById(id));

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                                ownerId, employerId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_getEmployerVacancy_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long id = 1L;

        mvc.perform(MockMvcRequestBuilders.get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                                ownerId, employerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));

    }

    @Test
    public void test_NotFound_getByEmployerVacancy_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long id = 10000000L;

        mvc.perform(get(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}",
                        ownerId, employerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Vacancy not found\"")));

    }

    @Test
    public void test_Create_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies", ownerId, employerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getVacancy()))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("Vacancy for employer Admin Carol successfully created.",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_BadRequest_Create_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        VacancyRequest vacancyRequest = VacancyRequest.builder().build();
        vacancyRequest.setCategory(Category.JAVA);
        vacancyRequest.setCity("city");

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies", ownerId, employerId)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(vacancyRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"BAD_REQUEST\"")));
    }

    @Test
    public void test_Forbidden_Create_Candidate() throws Exception {
        long ownerId = 5L;
        long employerId = 3L;

        mvc.perform(post(BASIC_URL + "/employer-profile/{employer-id}/vacancies", ownerId, employerId)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getVacancy()))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Update_Employer() throws Exception {
        long ownerID = 6L;
        long employerId = 3L;
        long id = 6L;

        mvc.perform(put(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}", ownerID, employerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getVacancy()))
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Vacancy for employer Violet Jackson successfully updated.",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long ownerID = 1L;
        long employerId = 1L;
        long id = 4L;

        mvc.perform(put(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}", ownerID, employerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getVacancy()))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                                .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_BadRequest_Update_Employer() throws Exception {
        long ownerId = 6L;
        long employerId = 3L;
        long id = 8L;
        VacancyRequest vacancyRequest = VacancyRequest.builder().build();
        vacancyRequest.setCategory(Category.JAVA);
        vacancyRequest.setCity("city");

        mvc.perform(put(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}", ownerId, employerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(vacancyRequest))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"BAD_REQUEST\"")));
    }

    @Test
    public void test_Delete_Employer() throws Exception {
        long ownerID = 6L;
        long employerId = 3L;
        long id = 7L;

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}", ownerID, employerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("Vacancy successfully deleted",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Delete_Admin() throws Exception {
        long ownerID = 1L;
        long candidateID = 1L;
        long id = 5L;

        mvc.perform(delete(BASIC_URL + "/employer-profile/{employer-id}/vacancies/{id}", ownerID, candidateID, id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                                .contains(ACCESS_DENIED)));
    }

    private List<CutVacancyResponse> getSortedVacancies(long id1, long id2, long id3, long id4, long id5) {
        return Stream.of(vacancyService.readById(id1), vacancyService.readById(id2),
                vacancyService.readById(id3), vacancyService.readById(id4), vacancyService.readById(id5))
                .map(mapper::getCutVacancyResponseFromVacancy)
                .toList();
    }

    private VacancyRequest getVacancy() {
        VacancyRequest vacancyRequest = VacancyRequest.builder().build();
        vacancyRequest.setCategory(Category.JAVA);
        vacancyRequest.setCity("city");
        vacancyRequest.setDomain(JobDomain.ADULT);
        vacancyRequest.setEnglishLevel(LanguageLevel.PRE_INTERMEDIATE);
        vacancyRequest.setDetailDescription("Detail");
        vacancyRequest.setSalaryFrom(1000);
        vacancyRequest.setSalaryTo(1500);
        vacancyRequest.setLookingFor("Look");
        vacancyRequest.setWorkExperience(1.7);
        vacancyRequest.setCountry("Ukraine");
        vacancyRequest.setWorkMode(WorkMode.HYBRID);

        return vacancyRequest;
    }
}
