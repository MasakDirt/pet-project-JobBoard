package com.board.job.model;

import com.board.job.model.entity.Role;
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
public class RoleTests {
    private static Role role;

    @BeforeAll
    public static void init() {
        role = new Role();
        role.setId(1L);
        role.setName("USER");
    }

    @Test
    public void test_Valid_Role() {
        assertEquals(0, getViolation(role).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForName")
    public void test_Invalid_Role_Name(String name, String error) {
        role.setName(name);

        assertEquals(1, getViolation(role).size());
        assertEquals(error, getViolation(role).iterator().next().getInvalidValue());
        assertEquals("The role name must be in upper case!",
                getViolation(role).iterator().next().getMessage());

        role.setName("USER");
    }

    private static Stream<Arguments> argumentsForName() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("user", "user"),
                Arguments.of("User", "User"),
                Arguments.of("usE12r", "usE12r"),
                Arguments.of("user.1!#", "user.1!#")
        );
    }
}
