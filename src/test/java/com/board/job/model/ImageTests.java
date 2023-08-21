package com.board.job.model;

import com.board.job.model.entity.Image;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.board.job.model.ValidationHelper.getViolation;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ImageTests {
    private static Image image;

    @BeforeAll
    public static void init() {
        image = new Image();
        image.setId(1L);
        image.setFilename("file");
    }

    @Test
    public void test_Valid_Image() {
        assertEquals(0, getViolation(image).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForFilename")
    public void test_Invalid_Image_FileName(String filename, String error) {
        image.setFilename(filename);

        assertEquals(1, getViolation(image).size());
        assertEquals(error, getViolation(image).iterator().next().getInvalidValue());
        assertEquals("The file name cannot be 'blank', please write it!",
                getViolation(image).iterator().next().getMessage());

        image.setFilename("new file");
    }

    private static Stream<Arguments> argumentsForFilename() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "")
        );
    }
}
