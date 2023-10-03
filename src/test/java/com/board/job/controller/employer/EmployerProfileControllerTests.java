package com.board.job.controller.employer;

import com.board.job.model.dto.employer_profile.EmployerProfileRequest;
import com.board.job.model.dto.employer_profile.EmployerProfileResponse;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.employer.EmployerProfileMapper;
import com.board.job.service.UserService;
import com.board.job.service.employer.EmployerProfileService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EmployerProfileControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}/employer-profiles";

    private final MockMvc mvc;
    private final EmployerProfileService employerProfileService;
    private final EmployerProfileMapper mapper;
    private final UserService userService;

    private String adminToken;
    private String larryToken;
    private String violetToken;

    @Autowired
    public EmployerProfileControllerTests(MockMvc mvc, EmployerProfileService service, EmployerProfileMapper mapper,
                                          UserService userService) {
        this.mvc = mvc;
        this.employerProfileService = service;
        this.mapper = mapper;
        this.userService = userService;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        larryToken = getUserToken(mvc, "larry@mail.co", "2222");
        violetToken = getUserToken(mvc, "violet@mail.co", "6666");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(employerProfileService);
        assertNotNull(mapper);
        assertNotNull(userService);
    }

    @Test
    public void test_GetById_User() throws Exception {
        long ownerId = 2L;
        long id = 2L;
        EmployerProfileResponse expected = mapper.getEmployerProfileResponseFromEmployerProfile(
                employerProfileService.readById(id)
        );

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + larryToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected), result.getResponse().getContentAsString()));
    }

    @Test
    public void test_GetById_Admin() throws Exception {
        long ownerId = 6L;
        long id = 3L;
        EmployerProfileResponse expected = mapper.getEmployerProfileResponseFromEmployerProfile(
                employerProfileService.readById(id)
        );

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_GetById_User() throws Exception {
        long ownerId = 6L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + violetToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_NotFound_GetById_User() throws Exception {
        long ownerId = 6L;
        long id = 100L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + violetToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Employer profile not found\"")));
    }

    @Test
    public void test_Create() throws Exception {
        String userToken = registerUserAndGetHisToken(mvc);
        long ownerID = userService.readByEmail("maks@mail.co").getId();

        mvc.perform(post(BASIC_URL, ownerID)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getProfile()))
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("Employer profile for user Maks Korniev successfully created",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Create_USER() throws Exception {
        long ownerID = 3L;

        mvc.perform(post(BASIC_URL, ownerID)
                        .header("Authorization", "Bearer " + larryToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getProfile()))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Update_USER() throws Exception {
        long ownerID = 6L;
        long id = 3L;
        User user = userService.readById(ownerID);

        mvc.perform(put(BASIC_URL + "/{id}", ownerID, id)
                        .header("Authorization", "Bearer " + violetToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getProfile()))
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(String.format("Employer profile for user %s successfully updated", user.getName()),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(put(BASIC_URL + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(getProfile()))
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                                .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Delete_USER() throws Exception {
        long ownerID = 6L;
        long id = 3L;
        User user = userService.readById(ownerID);

        mvc.perform(delete(BASIC_URL + "/{id}", ownerID, id)
                        .header("Authorization", "Bearer " + violetToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(String.format("Employer profile for user %s successfully deleted", user.getName()),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Delete_USER() throws Exception {
        long ownerID = 2L;
        long candidateID = 3L;

        mvc.perform(delete(BASIC_URL + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + larryToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    private EmployerProfileRequest getProfile() {
        EmployerProfileRequest request = EmployerProfileRequest.builder().build();
        request.setEmployerName("New name");
        request.setLinkedInProfile("www.linkedin.co/DKD'");
        request.setPhone("0348230345");
        request.setCompanyName("COMPANY OF IT");
        request.setPositionInCompany("HR");
        request.setTelegram("@telega");

        return request;
    }
}
