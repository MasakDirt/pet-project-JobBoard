package com.board.job.service;

import com.board.job.model.entity.PDF_File;
import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.service.candidate.CandidateContactService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import com.google.common.io.Files;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class PDFServiceTests {
    private final PDFService pdfService;
    private final CandidateContactService candidateContactService;
    private static CandidateContact candidateContact;

    @Autowired
    public PDFServiceTests(PDFService pdfService, CandidateContactService candidateContactService) {
        this.pdfService = pdfService;
        this.candidateContactService = candidateContactService;
    }

    @BeforeAll
    public static void setCandidateContact() {
        candidateContact = new CandidateContact();
        candidateContact.setGithubUrl("www.github.co/OKDFmkmfkfmdkdmf");
        candidateContact.setLinkedInProfile("www.linkedin.co/FLKefjiJDO:k");
        candidateContact.setTelegram("@telegramCandid");
        candidateContact.setPortfolioUrl("www.portfolio.co/FLepflpflpldf;fs");
        candidateContact.setPhone("0984563323");
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(pdfService).isNotNull();
    }

    @Test
    public void test_Valid_Create() throws Exception {
        File file = new File("files/pdf/CV_Maksym_Korniev.pdf");
        long candidateId = candidateContactService.create(2L, candidateContact).getId();

        PDF_File expected = new PDF_File();
        expected.setFileContent(Files.toByteArray(file));


        PDF_File actual = pdfService.create(candidateId, Files.toByteArray(file));
        expected.setId(actual.getId());

        assertEquals(expected, actual,
                "We created 2 files with same file path, so they must be same!");
    }

    @Test
    public void test_Invalid_Create() {
        assertThrows(DataIntegrityViolationException.class, () -> pdfService.create(2L, null),
                "Data integrity validation exception will be thrown, because we pass null in method.");
    }

    @Test
    public void test_Valid_ReadById() throws Exception {
        String fileName = "files/pdf/Certificate_Maksym_Korniev.pdf";
        long candidateId = candidateContactService.create(2L, candidateContact).getId();
        PDF_File expected = pdfService.create(candidateId, Files.toByteArray(new File(fileName)));

        PDF_File actual = pdfService.readById(expected.getId());

        assertEquals(Arrays.toString(Files.toByteArray(new File(fileName))), Arrays.toString(actual.getFileContent()),
                "Byte array of files must be equal, because it same files");
        assertEquals(expected, actual,
                "Two entities must be equal because it creates by same file and read with same id.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> pdfService.readById(0),
                "Entity not found exception will be thrown because we have no pdf file with id 0.");
    }

    @Test
    public void test_Valid_Update() throws Exception {
        String newFileName = "files/pdf/CV_Maksym_Korniev.pdf";
        String oldFileName = "files/pdf/Certificate_Maksym_Korniev.pdf";
        long candidateId = candidateContactService.create(2L, candidateContact).getId();

        PDF_File unexpected = pdfService.create(candidateId, Files.toByteArray(new File(oldFileName)));
        long oldId = unexpected.getId();

        PDF_File actual = pdfService.update(oldId, Files.toByteArray(new File(newFileName)));

        assertAll(
                () -> assertEquals(oldId, actual.getId(),
                        "Id`s isn`t update when we update file, so here they must be sames"),

                () -> assertNotEquals(Arrays.toString(Files.toByteArray(new File(oldFileName))),
                        Arrays.toString(actual.getFileContent()),
                        "It`s old file, so they shouldn`t be sames"),

                () -> assertEquals(Arrays.toString(Files.toByteArray(new File(newFileName))),
                        Arrays.toString(actual.getFileContent()),
                        "It`s updated entity, so new file name must be equal to entities.")
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(EntityNotFoundException.class, () -> pdfService.update(0, new byte[0]),
                "Entity not found exception will be thrown because we have no pdf file with id 0.");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 1L;

        pdfService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> pdfService.readById(id),
                "Entity not found exception will be thrown because we delete file with id 1,so now we have no this.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> pdfService.delete(0),
                "Entity not found exception will be thrown because we have no file with id 0.");
    }
}
