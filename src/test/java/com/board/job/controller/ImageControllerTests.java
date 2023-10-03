package com.board.job.controller;

import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.service.ImageService;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateContactService;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;

import static com.board.job.controller.PdfFileControllerTests.getCandidate;
import static com.board.job.helper.HelperForTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class ImageControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}";

    private final MockMvc mvc;
    private final ImageService imageService;
    private final UserService userService;
    private final CandidateContactService candidateContactService;
    private final EmployerProfileService employerProfileService;

    private String adminToken;
    private String candidateToken;
    private String employerToken;

    @Autowired
    public ImageControllerTests(MockMvc mvc, ImageService imageService, UserService userService,
                                CandidateContactService candidateContactService,
                                EmployerProfileService employerProfileService) {
        this.mvc = mvc;
        this.imageService = imageService;
        this.userService = userService;
        this.candidateContactService = candidateContactService;
        this.employerProfileService = employerProfileService;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        candidateToken = getUserToken(mvc, "nikole@mail.co", "3333");
        employerToken = getUserToken(mvc, "larry@mail.co", "2222");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(imageService);
        assertNotNull(userService);
        assertNotNull(candidateContactService);
        assertNotNull(employerProfileService);
    }

    @Test
    public void test_getByIdCandidateContactsImage_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 2L;

        File file = new File("CandidateImage.jpg");
        byte[] imageBytes = Files.readAllBytes(file.toPath());

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", ownerId, candidateId, imageId)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=CandidateImage.jpg"))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    public void test_Forbidden_getByIdCandidateContactsImage_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 2L;
        long imageId = 1L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", ownerId, candidateId, imageId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains(ACCESS_DENIED)
                ));
    }

    @Test
    public void test_NotFound_getByIdCandidateContactsImage_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 0L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", ownerId, candidateId, imageId)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains(imageNotFoundErrorMessage())
                ));
    }

    @Test
    public void test_getByIdEmployerProfileImage_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;
        long imageId = 1L;

        File file = new File("EmployerImage.jpg");
        byte[] imageBytes = Files.readAllBytes(file.toPath());

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", ownerId, employerId, imageId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=EmployerImage.jpg"))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    public void test_Forbidden_getByIdEmployerProfileImage_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 3L;
        long imageId = 2L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", ownerId, employerId, imageId)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains(ACCESS_DENIED)
                ));
    }

    @Test
    public void test_NotFound_getByIdEmployerProfileImage_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long imageId = 0L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", ownerId, employerId, imageId)
                        .header("Authorization", "Bearer " + employerToken)
                        .contentType(MediaType.IMAGE_JPEG))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(
                        result.getResponse().getContentAsString().contains(imageNotFoundErrorMessage())
                ));
    }

    @Test
    public void test_AddCandidatePhoto_User() throws Exception {
        String token = registerUserAndGetHisToken(mvc);
        long userId = userService.readByEmail("maks@mail.co").getId();
        long candidateId = candidateContactService.create(userId, getCandidate()).getId();

        String imageFilename = "files/photos/nicolas.jpg";
        File file = new File(imageFilename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                imageFilename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL + "/candidate-contacts/{candidate-id}/images", userId, candidateId)
                        .file(multipartFile)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("Your new profile picture successfully uploaded"));
    }

    @Test
    public void test_Forbidden_AddCandidatePhoto_Admin() throws Exception {
        long userId = 1L;
        long candidateId = 3L;

        String imageFilename = "files/photos/nicolas.jpg";
        File file = new File(imageFilename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                imageFilename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL + "/candidate-contacts/{candidate-id}/images", userId, candidateId)
                        .file(multipartFile)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED))
                );
    }

    @Test
    public void test_AddEmployerPhoto_User() throws Exception {
        String token = registerUserAndGetHisToken(mvc);
        long userId = userService.readByEmail("maks@mail.co").getId();
        long employerId = employerProfileService.create(userId, getEmployer()).getId();

        String imageFilename = "files/photos/violetPhoto.jpg";
        File file = new File(imageFilename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                imageFilename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL + "/employer-profiles/{employer-id}/images", userId, employerId)
                        .file(multipartFile)
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("Your new profile picture successfully uploaded"));
    }

    @Test
    public void test_Forbidden_AddEmployerPhoto_Admin() throws Exception {
        long userId = 1L;
        long employerId = 3L;

        String imageFilename = "files/photos/violetPhoto.jpg";
        File file = new File(imageFilename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                imageFilename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL + "/employer-profiles/{employer-id}/images", userId, employerId)
                        .file(multipartFile)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result ->
                        assertTrue(result.getResponse().getContentAsString().contains(ACCESS_DENIED))
                );
    }

    @Test
    public void test_Forbidden_UpdateCandidatePhoto_Candidate() throws Exception {
        long userId = 3L;
        long candidateId = 2L;
        long id = 3L;

        String filename = "files/photos/adminPhoto.jpg";
        File file = new File(filename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(put(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(multipartFile.getBytes())
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Forbidden_UpdateEmployerPhoto_Employer() throws Exception {
        long userId = 2L;
        long employerID = 2L;
        long id = 3L;

        String filename = "files/photos/adminPhoto.jpg";
        File file = new File(filename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(put(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", userId, employerID, id)
                        .header("Authorization", "Bearer " + candidateToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(multipartFile.getBytes())
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_DeleteCandidatePhoto_Candidate() throws Exception {
        long userId = 3L;
        long candidateId = 2L;
        long id = 2L;

        mvc.perform(delete(BASIC_URL + "/candidate-contacts/{candidate-id}/images//{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + candidateToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Your profile picture successfully deleted"));

        assertThrows(EntityNotFoundException.class, () -> imageService.readById(id));
    }

    @Test
    public void test_Forbidden_DeleteCandidatePhoto_Admin() throws Exception {
        long userId = 1L;
        long candidateId = 1L;
        long id = 2L;

        mvc.perform(delete(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_DeleteEmployerPhoto_Employer() throws Exception {
        long userId = 2L;
        long employerId = 2L;
        long id = 5L;

        mvc.perform(delete(BASIC_URL + "/employer-profiles/{employer-id}/images//{id}", userId, employerId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Your profile picture successfully deleted"));

        assertThrows(EntityNotFoundException.class, () -> imageService.readById(id));
    }

    @Test
    public void test_Forbidden_DeleteEmployerPhoto_Employer() throws Exception {
        long userId = 2L;
        long candidateId = 2L;
        long id = 1L;

        mvc.perform(delete(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + employerToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    private static String imageNotFoundErrorMessage() {
        return "\"status\":\"NOT_FOUND\",\"message\":\"Image not found\"";
    }

    private EmployerProfile getEmployer() {
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setEmployerName("New ");
        employerProfile.setPhone("0853452345");
        employerProfile.setLinkedInProfile("www.linkedin.co");
        employerProfile.setTelegram("@teleg");
        employerProfile.setCompanyName("Company Soft Critical");
        employerProfile.setPositionInCompany("Manager");

        return employerProfile;
    }
}
