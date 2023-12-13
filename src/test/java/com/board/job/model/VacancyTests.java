package com.board.job.model;

import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
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
public class VacancyTests {
    private static Vacancy vacancy;

    @BeforeAll
    public static void init() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setLookingFor("Junior Java Developer");
        vacancy.setDomain(JobDomain.BIG_DATA);
        vacancy.setDetailDescription("Hello, it`s company...");
        vacancy.setCategory(Category.JAVA);
        vacancy.setWorkMode(WorkMode.CHOICE_OF_CANDIDATE);
        vacancy.setCountry("Ukraine");
        vacancy.setCity("Kyiv");
        vacancy.setSalaryFrom(300);
        vacancy.setSalaryTo(800);
        vacancy.setWorkExperience(0.5);
        vacancy.setEnglishLevel(LanguageLevel.UPPER_INTERMEDIATE);
    }

    @Test
    public void testValidVacancy() {
        assertEquals(0, getViolation(vacancy).size());
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidVacancyLookingFor(String lookingFor, String error) {
        vacancy.setLookingFor(lookingFor);

        assertEquals(1, getViolation(vacancy).size());
        assertEquals(error, getViolation(vacancy).iterator().next().getInvalidValue());
        assertEquals("Here must be the candidate that you are looking for.",
                getViolation(vacancy).iterator().next().getMessage());

        vacancy.setLookingFor("Junior Java Developer");
    }

    @Test
    public void testInvalidVacancyDomain() {
        vacancy.setDomain(null);

        assertEquals(1, getViolation(vacancy).size());

        vacancy.setDomain(JobDomain.BIG_DATA);
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidVacancyDescription(String description, String error) {
        vacancy.setDetailDescription(description);

        assertEquals(1, getViolation(vacancy).size());
        assertEquals(error, getViolation(vacancy).iterator().next().getInvalidValue());
        assertEquals("Your vacancy description must contain detailed description of this.",
                getViolation(vacancy).iterator().next().getMessage());

        vacancy.setDetailDescription("Hello, it`s company...");
    }

    @Test
    public void testInvalidVacancyCategory() {
        vacancy.setCategory(null);

        assertEquals(1, getViolation(vacancy).size());

        vacancy.setCategory(Category.JAVA);
    }

    @Test
    public void testInvalidVacancyWorkMode() {
        vacancy.setWorkMode(null);

        assertEquals(1, getViolation(vacancy).size());

        vacancy.setWorkMode(WorkMode.CHOICE_OF_CANDIDATE);
    }

    @ParameterizedTest
    @MethodSource("argumentsForStrings")
    public void testInvalidVacancyCountry(String country, String error) {
        vacancy.setCountry(country);

        assertEquals(1, getViolation(vacancy).size());
        assertEquals(error, getViolation(vacancy).iterator().next().getInvalidValue());
        assertEquals("You should declare country where candidate must be work.",
                getViolation(vacancy).iterator().next().getMessage());

        vacancy.setCountry("Ukraine");
    }

    @Test
    public void testInvalidVacancyWorkExperience() {
        vacancy.setWorkExperience(-0.2);

        assertEquals(1, getViolation(vacancy).size());
        assertEquals("The work experience must be at least 0 years.",
                getViolation(vacancy).iterator().next().getMessage());

        vacancy.setWorkExperience(0.5);
    }

    @Test
    public void testInvalidVacancyEnglishLevel() {
        vacancy.setEnglishLevel(null);

        assertEquals(1, getViolation(vacancy).size());

        vacancy.setEnglishLevel(LanguageLevel.PRE_INTERMEDIATE);
    }

    private static Stream<Arguments> argumentsForStrings() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.arguments("", "")
        );
    }
}
