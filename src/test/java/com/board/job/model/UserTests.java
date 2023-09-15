package com.board.job.model;

import com.board.job.model.entity.User;
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
public class UserTests {
    private static User user;

    @BeforeAll
    public static void init() {
        user = new User();
        user.setId(1L);
        user.setFirstName("First");
        user.setLastName("Last");
        user.setEmail("user@mail.co");
        user.setPassword("password");
    }

    @Test
    public void test_Valid_User() {
        assertEquals(0, getViolation(user).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForNames")
    public void test_Invalid_FirstName(String firstName, String error) {
        user.setFirstName(firstName);

        assertEquals(1, getViolation(user).size());
        assertEquals(error, getViolation(user).iterator().next().getInvalidValue());
        assertEquals("First name must start with a capital letter and followed by one or more lowercase",
                getViolation(user).iterator().next().getMessage());

        user.setFirstName("First");
    }

    @ParameterizedTest
    @MethodSource("argumentsForNames")
    public void test_Invalid_LastName(String lastName, String error) {
        user.setLastName(lastName);

        assertEquals(1, getViolation(user).size());
        assertEquals(error, getViolation(user).iterator().next().getInvalidValue());
        assertEquals("Last name must start with a capital letter and followed by one or more lowercase",
                getViolation(user).iterator().next().getMessage());

        user.setLastName("Last");
    }

    @Test
    public void test_Invalid_FirstName_Null() {
        user.setFirstName(null);

        assertEquals(1, getViolation(user).size());
        assertEquals("Write the first name!",
                getViolation(user).iterator().next().getMessage());

        user.setFirstName("First");
    }

    @Test
    public void test_Invalid_LastName_Null() {
        user.setLastName(null);

        assertEquals(1, getViolation(user).size());
        assertEquals("Write the last name!",
                getViolation(user).iterator().next().getMessage());

        user.setLastName("Last");
    }

    @ParameterizedTest
    @MethodSource("argumentsForEmail")
    public void test_Invalid_Email(String email, String error) {
        user.setEmail(email);

        assertEquals(1, getViolation(user).size());
        assertEquals(error, getViolation(user).iterator().next().getInvalidValue());
        assertEquals("Must be a valid e-mail address",
                getViolation(user).iterator().next().getMessage());

        user.setEmail("user@mail.co");
    }

    @ParameterizedTest
    @MethodSource("argumentsForPassword")
    public void test_Invalid_Password(String password, String error) {
        user.setPassword(password);

        assertEquals(1, getViolation(user).size());
        assertEquals(error, getViolation(user).iterator().next().getInvalidValue());
        assertEquals("The password cannot be 'blank'",
                getViolation(user).iterator().next().getMessage());

        user.setPassword("password");
    }

    private static Stream<Arguments> argumentsForNames() {
        return Stream.of(
                Arguments.arguments("", ""),
                Arguments.arguments("name", "name"),
                Arguments.arguments("nAme", "nAme"),
                Arguments.arguments("namE", "namE"),
                Arguments.arguments("nAME", "nAME"),
                Arguments.arguments("--", "--"),
                Arguments.arguments("!!", "!!")
        );
    }

    private static Stream<Arguments> argumentsForEmail() {
        return Stream.of(
                Arguments.arguments("", ""),
                Arguments.arguments("email", "email"),
                Arguments.arguments("ema@il", "ema@il"),
                Arguments.arguments("user@.", "user@."),
                Arguments.arguments("user@mail", "user@mail")

        );
    }

    private static Stream<Arguments> argumentsForPassword() {
        return Stream.of(
                Arguments.arguments(null, null),
                Arguments.arguments("", "")

        );
    }
}
