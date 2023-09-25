package com.board.job.controller;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.service.PDFService;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateContactService;
import com.google.common.io.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
    private final PDFService pdfService;
    private final UserService userService;
    private final CandidateContactService candidateContactService;

    private String adminToken;
    private String donaldToken;

    @Autowired
    public PdfFileControllerTests(MockMvc mvc, PDFService pdfService, UserService userService,
                                  CandidateContactService candidateContactService) {
        this.mvc = mvc;
        this.pdfService = pdfService;
        this.userService = userService;
        this.candidateContactService = candidateContactService;
    }

    @BeforeEach
    public void initTokens() throws Exception {
        adminToken = getAdminToken(mvc);
        donaldToken = getUserToken(mvc, "donald@mail.co", "4444");
    }

    @Test
    public void test_Injected_Components() {
        assertNotNull(mvc);
        assertNotNull(pdfService);
        assertNotNull(userService);
    }

    @Test
    public void test_GetById_Admin() throws Exception {
        long ownerId = 1L;
        long candidateId = 3L;
        long id = 3L;

        File expected = pdfService.getPDFFile(id);

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, candidateId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isOk())
                .andExpectAll(
                        result -> assertEquals(expected.length(), result.getResponse().getContentLength()),
                        result -> assertEquals("application/pdf", result.getResponse().getContentType())
                );
    }

    @Test
    public void test_GetById_Candidate() throws Exception {
        long ownerId = 4L;
        long candidateId = 1L;
        long id = 1L;

        File expected = pdfService.getPDFFile(id);

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, candidateId, id)
                        .header("Authorization", "Bearer " + donaldToken)
                )
                .andExpect(status().isOk())
                .andExpectAll(
                        result -> assertEquals(expected.length(), result.getResponse().getContentLength()),
                        result -> assertEquals("application/pdf", result.getResponse().getContentType())
                );
    }

    @Test
    public void test_NotFound_GetById_Candidate() throws Exception {
        long ownerId = 4L;
        long candidateId = 1L;
        long id = 1000L;

        mvc.perform(get(BASIC_URL + "/{id}", ownerId, candidateId, id)
                        .header("Authorization", "Bearer " + donaldToken)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains("\"status\":\"NOT_FOUND\",\"message\":\"File not found\"")));
    }

    @Test
    public void test_Create_User() throws Exception {
        String token = registerUserAndGetHisToken(mvc);
        long userId = userService.readByEmail("maks@mail.co").getId();
        long candidateId = candidateContactService.create(userId, getCandidate()).getId();

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
                        .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("PDF file successfully uploaded"));
    }

    @Test
    public void test_Forbidden_Create_Admin() throws Exception {
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
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Forbidden_Update_Candidate() throws Exception {
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

        mvc.perform(put(BASIC_URL + "/{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + donaldToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .content(multipartFile.getBytes())
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    @Test
    public void test_Delete_Candidate() throws Exception {
        long userId = 4L;
        long candidateId = 3L;
        long id = 3L;

        mvc.perform(delete(BASIC_URL + "/{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + donaldToken)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("PDF file successfully deleted"));
    }

    @Test
    public void test_Forbidden_Delete_Admin() throws Exception {
        long userId = 1L;
        long candidateId = 1L;
        long id = 2L;

        mvc.perform(delete(BASIC_URL + "/{id}", userId, candidateId, id)
                        .header("Authorization", "Bearer " + adminToken)
                )
                .andExpect(status().isForbidden())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString()
                        .contains(ACCESS_DENIED)));
    }

    private CandidateContact getCandidate() {
        CandidateContact contact = new CandidateContact();
        contact.setCandidateName("New");
        contact.setEmail("email@mail.co");
        contact.setPhone("00489344355");
        contact.setGithubUrl("www.github.co/23424");
        contact.setTelegram("@tele");
        contact.setPortfolioUrl("www.portfolio.co/eorRIEOFk");
        contact.setLinkedInProfile("www.linkedin.co/OKFOkdld");

        return contact;
    }
}

