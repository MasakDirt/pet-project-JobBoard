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
        pdfFile.setFilename("new file");
        pdfFile.setFileContent(new byte[]{1, 2, 5, 7, 9});
    }

    @Test
    public void test_Valid_PDF_File() {
        assertEquals(0, getViolation(pdfFile).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForFilename")
    public void test_Invalid_PDF_File_Filename(String filename, String error) {
        pdfFile.setFilename(filename);

        assertEquals(1, getViolation(pdfFile).size());
        assertEquals(error, getViolation(pdfFile).iterator().next().getInvalidValue());
        assertEquals("The file name cannot be 'blank', please write it!",
                getViolation(pdfFile).iterator().next().getMessage());

        pdfFile.setFilename("new file");
    }

    private static Stream<Arguments> argumentsForFilename() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "")
        );
    }
}
