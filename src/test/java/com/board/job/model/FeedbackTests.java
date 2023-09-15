package com.board.job.model;

import com.board.job.model.entity.Feedback;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.board.job.config.HelperForTests.getViolation;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FeedbackTests {
    private static Feedback feedback;

    @BeforeAll
    public static void init() {
        feedback = new Feedback();
        feedback.setId("FIRSTId");
        feedback.setText("Hello, I`m...");
    }

    @Test
    public void test_Valid_Feedback() {
        assertEquals(0, getViolation(feedback).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForText")
    public void test_Invalid_FeedBack_Text(String text, String error) {
        feedback.setText(text);

        assertEquals(1, getViolation(feedback).size());
        assertEquals(error, getViolation(feedback).iterator().next().getInvalidValue());
        assertEquals("You should write text about why you the best candidate for this vacation.",
                getViolation(feedback).iterator().next().getMessage());

        feedback.setText("Hello, I`m...");
    }

    private static Stream<Arguments> argumentsForText() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "")
        );
    }
}
