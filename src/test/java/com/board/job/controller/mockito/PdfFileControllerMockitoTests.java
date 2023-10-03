package com.board.job.controller.mockito;

import com.board.job.controller.PDFFileController;
import com.board.job.model.entity.PDF_File;
import com.board.job.service.PDFService;
import com.google.common.io.Files;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class PdfFileControllerMockitoTests {
    @InjectMocks
    private PDFFileController controller;
    @Mock
    private PDFService service;
    @Mock
    private Authentication authentication;

    @Test
    public void test_NotFound_GetById() {
        long ownerId = 5L;
        long id = 100L;
        when(service.getPDFFile(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getById(ownerId, id, authentication));

        verify(service, times(1)).getPDFFile(id);
    }

    @Test
    public void test_Create() throws Exception {
        long candidateId = 3L;
        long id = 34L;
        String filename = "files/pdf/Certificate_Maksym_Korniev.pdf";
        byte[] bytes = Files.toByteArray(new File(filename));

        PDF_File pdfFile = new PDF_File();
        pdfFile.setFileContent(bytes);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                bytes
        );

        when(service.create(id, bytes)).thenReturn(pdfFile);
        controller.create(candidateId, id, multipartFile, authentication);

        verify(service, times(1)).create(id, bytes);
    }

    @Test
    public void test_Update() throws Exception {
        long ownerId = 4L;
        long candidateId = 3L;
        long id = 34L;
        String filename = "files/pdf/Certificate_Maksym_Korniev.pdf";
        byte[] bytes = Files.toByteArray(new File(filename));

        PDF_File pdfFile = new PDF_File();
        pdfFile.setFileContent(bytes);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                bytes
        );

        when(service.update(id, bytes)).thenReturn(pdfFile);
        controller.update(ownerId, candidateId, id, multipartFile, authentication);

        verify(service, times(1)).update(id, bytes);
    }

    @Test
    public void test_Delete() {
        long ownerId = 4L;
        long candidateId = 3L;
        long id = 3L;
        controller.delete(ownerId, candidateId, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
