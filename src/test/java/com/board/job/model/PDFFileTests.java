package com.board.job.model;

import com.board.job.model.entity.PDF_File;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.board.job.helper.HelperForTests.getViolation;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PDFFileTests {
    private static PDF_File pdfFile;

    @BeforeAll
    public static void init() {
        pdfFile = new PDF_File();
        pdfFile.setId(1L);
        pdfFile.setFileContent(new byte[]{1, 2, 5, 7, 9});
    }

    @Test
    public void testValidPDFFile() {
        assertEquals(0, getViolation(pdfFile).size());
    }
}
