package com.board.job.model.candidate;

import com.board.job.model.entity.candidate.CandidateContacts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.board.job.model.ValidationHelper.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CandidateContactsTests {
    private static CandidateContacts candidateContacts;

    @BeforeAll
    public static void init() {
        candidateContacts = new CandidateContacts();
        candidateContacts.setId(1L);
        candidateContacts.setCandidateName("Paula Flore");
        candidateContacts.setEmail("candidate@mail.co");
        candidateContacts.setPhone("098xxx98xx");
        candidateContacts.setTelegram("@candidate234");
        candidateContacts.setLinkedInProfile("www.linkedin.co/giregoijeigru");
        candidateContacts.setGithubUrl("www.github.com/MasakDirt");
        candidateContacts.setPortfolioUrl("www.portfolio.co/fmgogo");
    }

    @Test
    public void test_Valid_CandidateContacts() {
        assertEquals(0, getViolation(candidateContacts).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void test_Invalid_CandidateContacts_CandidateName(String candidateName, String error) {
        candidateContacts.setCandidateName(candidateName);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("The candidate name cannot be 'blank'",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setCandidateName("Paula Flore");
    }

    @ParameterizedTest
    @MethodSource("argumentsForEmail")
    public void test_Invalid_Email(String email, String error) {
        candidateContacts.setEmail(email);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("Must be a valid e-mail address",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setEmail("candidate@mail.co");
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void test_Invalid_CandidateContacts_Phone(String phone, String error) {
        candidateContacts.setPhone(phone);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("You must write valid phone number.",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setPhone("098xxx98xx");
    }

    @ParameterizedTest
    @MethodSource("argumentsForTelegram")
    public void test_Invalid_CandidateContacts_Telegram(String telegram, String error) {
        candidateContacts.setTelegram(telegram);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("The telegram account must started with '@' character",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setTelegram("@candidate234");
    }

    @ParameterizedTest
    @MethodSource("argumentsForLinkedIn")
    public void test_Invalid_CandidateContacts_LinkedIn(String linkedIn, String error) {
        candidateContacts.setLinkedInProfile(linkedIn);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("Must be a valid LinkedIn link!",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setLinkedInProfile("www.linkedin.co/giregoijeigru");
    }

    @ParameterizedTest
    @MethodSource("argumentsForGitHub")
    public void test_Invalid_CandidateContacts_GitHub(String github, String error) {
        candidateContacts.setGithubUrl(github);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("Must be a valid GitHub link!",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setGithubUrl("www.github.com/MasakDirt");
    }

    @ParameterizedTest
    @MethodSource("argumentsForPortfolio")
    public void test_Invalid_CandidateContacts_Portfolio(String portfolio, String error) {
        candidateContacts.setPortfolioUrl(portfolio);

        assertEquals(1, getViolation(candidateContacts).size());
        assertEquals(error, getViolation(candidateContacts).iterator().next().getInvalidValue());
        assertEquals("Must be a valid portfolio link!",
                getViolation(candidateContacts).iterator().next().getMessage());

        candidateContacts.setPortfolioUrl("www.portfolio.co/fmgogo");
    }

    private static Stream<Arguments> argumentsForStrings() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "")
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

    private static Stream<Arguments> argumentsForTelegram() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("can", "can"),
                Arguments.of("can@", "can@"),
                Arguments.of("can@didate", "can@didate")
        );
    }

    private static Stream<Arguments> argumentsForLinkedIn() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("w", "w"),
                Arguments.of("www", "www"),
                Arguments.of("www.lin", "www.lin"),
                Arguments.of("www.linkedin.", "www.linkedin."),
                Arguments.of("www.person.co", "www.person.co")
        );
    }

    private static Stream<Arguments> argumentsForGitHub() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("w", "w"),
                Arguments.of("www", "www"),
                Arguments.of("www.git", "www.git"),
                Arguments.of("www.github.", "www.github."),
                Arguments.of("www.linkedin.co", "www.linkedin.co")
        );
    }

    private static Stream<Arguments> argumentsForPortfolio() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("w", "w"),
                Arguments.of("www", "www"),
                Arguments.of("www.port", "www.port"),
                Arguments.of("www.portfolio.", "www.portfolio."),
                Arguments.of("www.github.co", "www.github.co")
        );
    }
}
