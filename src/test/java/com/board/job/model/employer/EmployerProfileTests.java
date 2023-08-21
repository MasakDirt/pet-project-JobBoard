package com.board.job.model.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.board.job.model.ValidationHelper.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class EmployerProfileTests {
    private static EmployerProfile employerProfile;

    @BeforeAll
    public static void init() {
        employerProfile = new EmployerProfile();
        employerProfile.setId(1L);
        employerProfile.setEmployerName("Lili Novus");
        employerProfile.setPositionInCompany("HR");
        employerProfile.setCompanyName("Company");
        employerProfile.setTelegram("@ccc");
        employerProfile.setPhone("0689875678");
        employerProfile.setLinkedInProfile("www.linkedin.co/fdlhkslhkkoGJOGo");
    }

    @Test
    public void test_Valid_EmployerProfile() {
        assertEquals(0, getViolation(employerProfile).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void test_Invalid_EmployerProfile_EmployerName(String name, String error) {
        employerProfile.setEmployerName(name);

        assertEquals(1, getViolation(employerProfile).size());
        assertEquals(error, getViolation(employerProfile).iterator().next().getInvalidValue());
        assertEquals("The employer name cannot be blank",
                getViolation(employerProfile).iterator().next().getMessage());

        employerProfile.setEmployerName("Lili Novus");
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void test_Invalid_EmployerProfile_PositionInCompany(String position, String error) {
        employerProfile.setPositionInCompany(position);

        assertEquals(1, getViolation(employerProfile).size());
        assertEquals(error, getViolation(employerProfile).iterator().next().getInvalidValue());
        assertEquals("You must write your position",
                getViolation(employerProfile).iterator().next().getMessage());

        employerProfile.setPositionInCompany("HR");
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void test_Invalid_EmployerProfile_CompanyName(String company, String error) {
        employerProfile.setCompanyName(company);

        assertEquals(1, getViolation(employerProfile).size());
        assertEquals(error, getViolation(employerProfile).iterator().next().getInvalidValue());
        assertEquals("You should write your company name",
                getViolation(employerProfile).iterator().next().getMessage());

        employerProfile.setCompanyName("Company");
    }

    @ParameterizedTest
    @MethodSource("argumentsForTelegram")
    public void test_Invalid_EmployerProfile_Telegram(String telegram, String error) {
        employerProfile.setTelegram(telegram);

        assertEquals(1, getViolation(employerProfile).size());
        assertEquals(error, getViolation(employerProfile).iterator().next().getInvalidValue());
        assertEquals("The telegram account must started with '@' character",
                getViolation(employerProfile).iterator().next().getMessage());

        employerProfile.setTelegram("@ccc");
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void test_Invalid_EmployerProfile_Phone(String phone, String error) {
        employerProfile.setPhone(phone);

        assertEquals(1, getViolation(employerProfile).size());
        assertEquals(error, getViolation(employerProfile).iterator().next().getInvalidValue());
        assertEquals("You must write valid phone number.",
                getViolation(employerProfile).iterator().next().getMessage());

        employerProfile.setPhone("0689875678");
    }

    @ParameterizedTest
    @MethodSource("argumentsForLinkedIn")
    public void test_Invalid_EmployerProfile_LinkedIn(String linkedIn, String error) {
        employerProfile.setLinkedInProfile(linkedIn);

        assertEquals(1, getViolation(employerProfile).size());
        assertEquals(error, getViolation(employerProfile).iterator().next().getInvalidValue());
        assertEquals("Must be a valid LinkedIn link!",
                getViolation(employerProfile).iterator().next().getMessage());

        employerProfile.setLinkedInProfile("www.linkedin.co/fdlhkslhkkoGJOGo");
    }

    private static Stream<Arguments> argumentsForStrings() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "")
        );
    }

    private static Stream<Arguments> argumentsForTelegram() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("hr", "hr"),
                Arguments.of("hr@", "hr@"),
                Arguments.of("hr@tr", "hr@tr")
        );
    }

    private static Stream<Arguments> argumentsForLinkedIn() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("w", "w"),
                Arguments.of("www", "www"),
                Arguments.of("www.c", "www.c"),
                Arguments.of("www.linkedin.", "www.linkedin."),
                Arguments.of("www.google.co", "www.google.co"),
                Arguments.of("www.apple.co", "www.apple.co")
        );
    }
}
