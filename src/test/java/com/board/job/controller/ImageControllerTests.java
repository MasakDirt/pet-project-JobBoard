package com.board.job.controller;

import com.board.job.model.entity.User;
import com.board.job.service.ImageService;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

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

    @Autowired
    public ImageControllerTests(MockMvc mvc, ImageService imageService, UserService userService) {
        this.mvc = mvc;
        this.imageService = imageService;
        this.userService = userService;
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(imageService);
        assertNotNull(userService);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_getNoImage() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 2L;

        mvc.perform(get(BASIC_URL + "/images/no-image", ownerId, candidateId, imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_getNoImageHeader() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 2L;

        mvc.perform(get(BASIC_URL + "/images/no-image/header", ownerId, candidateId, imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_getByIdCandidateContactsImage_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 2L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", ownerId, candidateId, imageId)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_getByIdCandidateContactsImage_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 2L;
        long imageId = 1L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", ownerId, candidateId, imageId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_NotFound_getByIdCandidateContactsImage_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 0L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}", ownerId, candidateId, imageId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_getByIdCandidateContactsImageHeader_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 2L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}/header", ownerId, candidateId, imageId)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_getByIdCandidateContactsImageHeader_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 2L;
        long imageId = 1L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}/header", ownerId, candidateId, imageId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_NotFound_getByIdCandidateContactsImageHeader_Candidate() throws Exception {
        long ownerId = 3L;
        long candidateId = 2L;
        long imageId = 0L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}/header", ownerId, candidateId, imageId))
                .andExpect(status().is2xxSuccessful())
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_getByIdEmployerProfileImage_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;
        long imageId = 1L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", ownerId, employerId, imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_NotFound_getByIdEmployerProfileImage_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long imageId = 0L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}", ownerId, employerId, imageId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_getByIdEmployerProfileImageHeader_Admin() throws Exception {
        long ownerId = 1L;
        long employerId = 1L;
        long imageId = 1L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}/header", ownerId, employerId, imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_NotFound_getByIdEmployerProfileImageHeader_Employer() throws Exception {
        long ownerId = 2L;
        long employerId = 2L;
        long imageId = 0L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}/header", ownerId, employerId, imageId))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
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
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_AddEmployerPhoto_Admin() throws Exception {
        User user = userService.readByEmail("admin@mail.co");
        long employerId = user.getCandidateContact().getId();

        String imageFilename = "files/photos/violetPhoto.jpg";
        File file = new File(imageFilename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                imageFilename,
                MediaType.TEXT_PLAIN_VALUE,
                com.google.common.io.Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL + "/employer-profiles/{employer-id}/images", user.getId(), employerId)
                        .file(multipartFile)
                )
                .andExpect(redirectedUrl(String.format("/api/users/%d/employer-profiles/%d", user.getId(), employerId)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
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
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
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

        mvc.perform(post(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}/edit", userId, candidateId, id)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(multipartFile.getBytes())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
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

        mvc.perform(post(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}/edit", userId, employerID, id)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(multipartFile.getBytes())
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "nikole@mail.co", roles = {"USER", "CANDIDATE"})
    public void test_DeleteCandidatePhoto_Candidate() throws Exception {
        long userId = 3L;
        long candidateId = 2L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images//{id}/delete", userId, candidateId, id))
                .andExpect(redirectedUrl(String.format("/api/users/%d/candidate-contacts/%d", userId, candidateId)));

        assertThrows(EntityNotFoundException.class, () -> imageService.readById(id));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"USER", "CANDIDATE", "EMPLOYER"})
    public void test_Forbidden_DeleteCandidatePhoto_Admin() throws Exception {
        long userId = 1L;
        long candidateId = 1L;
        long id = 2L;

        mvc.perform(get(BASIC_URL + "/candidate-contacts/{candidate-id}/images/{id}/delete", userId, candidateId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_DeleteEmployerPhoto_Employer() throws Exception {
        long userId = 2L;
        long employerId = 2L;
        long id = 5L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images//{id}/delete", userId, employerId, id))
                .andExpect(redirectedUrl(String.format("/api/users/%d/employer-profiles/%d", userId, employerId)));

        assertThrows(EntityNotFoundException.class, () -> imageService.readById(id));
    }

    @Test
    @WithMockUser(username = "larry@mail.co", roles = {"USER", "EMPLOYER"})
    public void test_Forbidden_DeleteEmployerPhoto_Employer() throws Exception {
        long userId = 2L;
        long candidateId = 2L;
        long id = 1L;

        mvc.perform(get(BASIC_URL + "/employer-profiles/{employer-id}/images/{id}/delete", userId, candidateId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}
