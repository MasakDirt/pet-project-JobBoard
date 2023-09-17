package com.board.job.service.mockito;

import com.board.job.model.entity.PDF_File;
import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.repository.PDFRepository;
import com.board.job.service.PDFService;
import com.board.job.service.candidate.CandidateContactService;
import com.google.common.io.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class PdfServiceMockitoTests {
    @InjectMocks
    private PDFService pdfService;
    @Mock
    private PDFRepository pdfRepository;
    @Mock
    private CandidateContactService candidateContactService;

    @Test
    public void test_Create() throws Exception {
        byte[] bytes = Files.toByteArray(new File("files/pdf/CV_Maksym_Korniev.pdf"));

        long candidateId = 2L;
        PDF_File file = new PDF_File();
        file.setFileContent(bytes);

        when(candidateContactService.readById(candidateId)).thenReturn(new CandidateContact());
        pdfService.create(candidateId, bytes);

        verify(pdfRepository, times(1)).save(file);
    }

    @Test
    public void test_ReadById() {
        long id = 3L;
        PDF_File file = new PDF_File();

        when(pdfRepository.findById(id)).thenReturn(Optional.of(file));
        PDF_File actual = pdfService.readById(id);

        assertEquals(file, actual);
    }

    @Test
    public void test_GetPdfFile() throws Exception{
        long id = 4L;
        PDF_File pdfFile = new PDF_File();
        pdfFile.setId(id);
        pdfFile.setFileContent(Files.toByteArray(new File("files/pdf/CV_Maksym_Korniev.pdf")));

        when(pdfRepository.findById(id)).thenReturn(Optional.of(pdfFile));
        pdfService.getPDFFile(id);
    }

    @Test
    public void test_Update() throws Exception {
        byte[] bytes = Files.toByteArray(new File("files/pdf/CV_Maksym_Korniev.pdf"));
        PDF_File file = new PDF_File();
        file.setFileContent(bytes);

        when(pdfRepository.findById(1L)).thenReturn(Optional.of(file));
        PDF_File actual = pdfService.update(1L, bytes);

        verify(pdfRepository, times(1)).save(file);

        assertNotEquals(file, actual);
    }

    @Test
    public void test_Delete() {
        when(pdfRepository.findById(5L)).thenReturn(Optional.of(new PDF_File()));
        pdfService.delete(5L);

        verify(pdfRepository, times(1)).delete(new PDF_File());
    }
}
