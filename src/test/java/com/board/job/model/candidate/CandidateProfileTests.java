package com.board.job.model.candidate;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.board.job.helper.HelperForTests.getViolation;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CandidateProfileTests {
    private static CandidateProfile candidateProfile;

    @BeforeAll
    public static void init() {
        candidateProfile = new CandidateProfile();
        candidateProfile.setId(1L);
        candidateProfile.setPosition("JavaScript Junior Developer");
        candidateProfile.setSalaryExpectations(450);
        candidateProfile.setWorkExperience(0);
        candidateProfile.setCountryOfResidence("Ukraine");
        candidateProfile.setCityOfResidence("Lviv");
        candidateProfile.setCategory(Category.JAVASCRIPT);
        candidateProfile.setEnglishLevel(LanguageLevel.UPPER_INTERMEDIATE);
        candidateProfile.setUkrainianLevel(LanguageLevel.ADVANCED_FLUENT);
        candidateProfile.setExperienceExplanation("I have no experience...");
        candidateProfile.setAchievements("My achievements: ...");
    }

    @Test
    public void testValidCandidateProfile() {
        assertEquals(0, getViolation(candidateProfile).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidCandidateProfilePosition(String position, String error) {
        candidateProfile.setPosition(position);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals(error, getViolation(candidateProfile).iterator().next().getInvalidValue());
        assertEquals("The position cannot be 'blank'",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setPosition("JavaScript Junior Developer");
    }

    @Test
    public void testInvalidCandidateProfileSalaryExpectations() {
        candidateProfile.setSalaryExpectations(-5);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals("The salary expectation must be at least 10$.",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setSalaryExpectations(450);
    }

    @Test
    public void testInvalidCandidateProfileWorkExperience() {
        candidateProfile.setWorkExperience(-0.1);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals("The work experience must be at least 0 years.",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setWorkExperience(0);
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidCandidateProfileCountryOfResidence(String countryOfResidence, String error) {
        candidateProfile.setCountryOfResidence(countryOfResidence);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals(error, getViolation(candidateProfile).iterator().next().getInvalidValue());
        assertEquals("The country of residence cannot be 'blank'",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setCountryOfResidence("Ukraine");
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidCandidateProfileCityOfResidence(String cityOfResidence, String error) {
        candidateProfile.setCityOfResidence(cityOfResidence);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals(error, getViolation(candidateProfile).iterator().next().getInvalidValue());
        assertEquals("The city of residence cannot be 'blank'",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setCityOfResidence("Lviv");
    }

    @Test
    public void testInvalidCandidateProfileCategory() {
        candidateProfile.setCategory(null);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals("You must pick one category",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setCategory(Category.JAVASCRIPT);
    }

    @Test
    public void testInvalidCandidateProfileEnglishLevel() {
        candidateProfile.setEnglishLevel(null);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals("You must select your level of english",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setEnglishLevel(LanguageLevel.UPPER_INTERMEDIATE);
    }

    @Test
    public void testInvalidCandidateProfileUkrainianLevel() {
        candidateProfile.setUkrainianLevel(null);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals("You must select your level of ukrainian",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setUkrainianLevel(LanguageLevel.ADVANCED_FLUENT);
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidCandidateProfileExperienceExplanation(String experience, String error) {
        candidateProfile.setExperienceExplanation(experience);

        assertEquals(1, getViolation(candidateProfile).size());
        assertEquals(error, getViolation(candidateProfile).iterator().next().getInvalidValue());
        assertEquals("Tell your experience here, if you have no, write about your study",
                getViolation(candidateProfile).iterator().next().getMessage());

        candidateProfile.setExperienceExplanation("I have no experience...");
    }

    private static Stream<Arguments> argumentsForStrings() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", "")
        );
    }
}
