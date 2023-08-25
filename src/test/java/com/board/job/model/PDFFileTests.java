package com.board.job.model;

import com.board.job.model.entity.PDF_File;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.board.job.model.ValidationHelper.getViolation;
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
    public void test_Valid_PDF_File() {
        assertEquals(0, getViolation(pdfFile).size());
    }
}
