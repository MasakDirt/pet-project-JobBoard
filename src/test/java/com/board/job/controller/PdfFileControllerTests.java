package com.board.job.controller;

import com.google.common.io.Files;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PdfFileControllerTests {
    private static final String BASIC_URL = "/api/users/{owner-id}/candidate-contacts/{contact-id}/pdfs";
    private final MockMvc mvc;

    @Autowired
    public PdfFileControllerTests(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    public void testInjectedComponents() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testGetByIdAdmin() throws Exception {
        long ownerId = 1L;
        long candidateId = 3L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, candidateId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "pdfBase64"))
                .andExpect(view().name("candidates/pdf-get"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testGetByIdCandidate() throws Exception {
        long ownerId = 4L;
        long candidateId = 1L;
        long id = 1L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, candidateId, id))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner", "pdfBase64"))
                .andExpect(view().name("candidates/pdf-get"));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testNotFoundGetByIdCandidate() throws Exception {
        long ownerId = 4L;
        long candidateId = 1L;
        long id = 1000L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, candidateId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testForbiddenCreateAdmin() throws Exception {
        long userId = 1L;
        long candidateId = 3L;

        String filename = "files/pdf/CV_Maksym_Korniev.pdf";
        File file = new File(filename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL, userId, candidateId)
                        .file(multipartFile)
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "helen@mail.co", roles = {"USER", "CANDIDATE"})
    public void testUpdateUser() throws Exception {
        long userId = 5L;
        long candidateId = 4L;
        long pdfId = 4L;

        String filename = "files/pdf/CV_Maksym_Korniev.pdf";
        File file = new File(filename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                Files.toByteArray(file)
        );

        mvc.perform(multipart(BASIC_URL + "/{id}/update", userId, candidateId, pdfId)
                        .file(multipartFile)
                )
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/candidate-contacts/%d", userId, candidateId)));
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testForbiddenUpdateCandidate() throws Exception {
        long userId = 4L;
        long candidateId = 3L;
        long id = 2L;

        String filename = "files/pdf/CV_Maksym_Korniev.pdf";
        File file = new File(filename);
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                Files.toByteArray(file)
        );

        mvc.perform(post(BASIC_URL + "/{id}/update", userId, candidateId, id)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(multipartFile.getBytes())
                )
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }

    @Test
    @WithMockUser(username = "donald@mail.co", roles = {"USER", "CANDIDATE"})
    public void testDeleteCandidate() throws Exception {
        long userId = 4L;
        long candidateId = 3L;
        long id = 3L;

        mvc.perform(get(BASIC_URL + "/{id}/delete", userId, candidateId, id))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(String.format("/api/users/%d/candidate-contacts/%d", userId, candidateId)));
    }

    @Test
    @WithMockUser(username = "admin@mail.co", roles = {"ADMIN", "CANDIDATE", "EMPLOYER"})
    public void testForbiddenDeleteAdmin() throws Exception {
        long userId = 1L;
        long candidateId = 1L;
        long id = 2L;

        mvc.perform(delete(BASIC_URL + "/{id}/update", userId, candidateId, id))
                .andExpect(getErrorView())
                .andExpect(getErrorAttributes());
    }
}

