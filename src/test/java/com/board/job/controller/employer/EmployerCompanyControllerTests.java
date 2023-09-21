package com.board.job.controller.employer;

import com.board.job.model.dto.employer_company.EmployerCompanyResponse;
import com.board.job.model.entity.User;
import com.board.job.model.mapper.employer.EmployerCompanyMapper;
import com.board.job.service.UserService;
import com.board.job.service.employer.EmployerCompanyService;
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
public class EmployerCompanyControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}/employer-companies";

    private final MockMvc mvc;
    private final EmployerCompanyService employerCompanyService;
    private final EmployerCompanyMapper mapper;
    private final UserService userService;

    private String adminToken;
    private String larryToken;
    private String violetToken;

    @Autowired
    public EmployerCompanyControllerTests(MockMvc mvc, EmployerCompanyService employerCompanyService,
                                          EmployerCompanyMapper mapper, UserService userService) {
        this.mvc = mvc;
        this.employerCompanyService = employerCompanyService;
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
        assertNotNull(employerCompanyService);
        assertNotNull(mapper);
        assertNotNull(userService);
    }

    @Test
    public void test_GetById_User() throws Exception {
        long ownerId = 2L;
        long id = 2L;
        EmployerCompanyResponse expected = mapper.getEmployerCompanyResponseFromEmployerCompany(
                employerCompanyService.readById(id)
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
        EmployerCompanyResponse expected = mapper.getEmployerCompanyResponseFromEmployerCompany(
                employerCompanyService.readById(id)
        );

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(asJsonString(expected).substring(0, 40),
                        result.getResponse().getContentAsString().substring(0, 40)));
    }

    @Test
    public void test_Forbidden_GetById_User() throws Exception {
        long ownerId = 6L;
        long id = 1L;

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
        long id = 10L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, id)
                        .header("Authorization", "Bearer " + violetToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"Employer company not found!\"")));
    }

    @Test
    public void test_Create() throws Exception {
        String userToken = registerUserAndGetHisToken(mvc);
        long ownerID = userService.readByEmail("maks@mail.co").getId();

        mvc.perform(post(BASIC_URL, ownerID)
                        .header("Authorization", "Bearer " + userToken)
                        .param("about_company", "About")
                        .param("web_site", "web")
                )
                .andExpect(status().isCreated())
                .andExpect(result -> assertEquals("Employer company for user Maks Korniev successfully created",
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Create_USER() throws Exception {
        long ownerID = 1L;

        mvc.perform(post(BASIC_URL, ownerID)
                        .header("Authorization", "Bearer " + larryToken)
                        .param("about_company", "About")
                        .param("web_site", "web")
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
                        .param("about_company", "Updated")
                        .param("web_site", "new")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(String.format("Employer company for user %s successfully updated", user.getName()),
                        result.getResponse().getContentAsString()));
    }

    @Test
    public void test_Forbidden_Update_ADMIN() throws Exception {
        long ownerID = 1L;
        long candidateID = 3L;

        mvc.perform(put(BASIC_URL + "/{id}", ownerID, candidateID)
                        .header("Authorization", "Bearer " + adminToken)
                        .param("about_company", "Updated")
                        .param("web_site", "new")
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
                .andExpect(result -> assertEquals(String.format("Employer company for user %s successfully deleted", user.getName()),
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
}
