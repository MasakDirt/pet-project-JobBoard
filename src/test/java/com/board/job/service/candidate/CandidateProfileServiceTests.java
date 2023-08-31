package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class CandidateProfileServiceTests {
    private final CandidateProfileService candidateProfileService;

    @Autowired
    public CandidateProfileServiceTests(CandidateProfileService candidateProfileService) {
        this.candidateProfileService = candidateProfileService;
    }

    @Test
    public void test_Injected_Components() {
        assertThat(candidateProfileService).isNotNull();
    }

    @Test
    public void test_Valid_Create() {
        long ownerId = 2L;

        List<CandidateProfile> before = candidateProfileService.getAll();

        CandidateProfile expected = new CandidateProfile();
        expected.setPosition("Position");
        expected.setSalaryExpectations(10000);
        expected.setWorkExperience(10);
        expected.setCountryOfResidence("USA");
        expected.setCityOfResidence("Los Angeles");
        expected.setCategory(Category.C_PLUS_PLUS);
        expected.setEnglishLevel(LanguageLevel.ADVANCED_FLUENT);
        expected.setUkrainianLevel(LanguageLevel.NO);
        expected.setExperienceExplanation("My experience");

        CandidateProfile actual = candidateProfileService.create(ownerId, expected);

        List<CandidateProfile> after = candidateProfileService.getAll();

        assertTrue(before.size() < after.size(),
                "Size of list that reads before creating must be smaller than after.");
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_Create() {
        long ownerId = 2L;
        assertThrows(NullPointerException.class, () -> candidateProfileService.create(ownerId, null),
                "Null pointer exception will be thrown, because we pass null user to method create.");

        assertThrows(ConstraintViolationException.class, () -> candidateProfileService.create(ownerId, new CandidateProfile()),
                "Constraint violation exception will be thrown, because we pass empty user.");
    }

    @Test
    public void test_Valid_ReadById() {
        long ownerId = 6L;

        CandidateProfile expected = new CandidateProfile();
        expected.setPosition("Position");
        expected.setSalaryExpectations(1234);
        expected.setWorkExperience(1);
        expected.setCountryOfResidence("Ukraine");
        expected.setCityOfResidence("Residence");
        expected.setCategory(Category.C_PLUS_PLUS);
        expected.setEnglishLevel(LanguageLevel.PRE_INTERMEDIATE);
        expected.setUkrainianLevel(LanguageLevel.ADVANCED_FLUENT);
        expected.setExperienceExplanation("My experience");

        expected = candidateProfileService.create(ownerId, expected);

        CandidateProfile actual = candidateProfileService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.readById(0),
                "Entity not found exception will be thrown because we have no candidate with id 0.");
    }

    @Test
    public void test_Valid_Update() {
        CandidateProfile unexpected = candidateProfileService.readById(1L);

        Category oldCategory = unexpected.getCategory();
        String oldPosition = unexpected.getPosition();
        LanguageLevel oldEnglish = unexpected.getEnglishLevel();

        unexpected.setPosition("New");
        unexpected.setCategory(Category.KOTLIN);
        unexpected.setEnglishLevel(LanguageLevel.ADVANCED_FLUENT);

        CandidateProfile actual = candidateProfileService.update(unexpected);

        assertAll(
                () -> assertEquals(unexpected.getId(), actual.getId()),
                () -> assertNotEquals(oldCategory, actual.getCategory()),
                () -> assertNotEquals(oldPosition, actual.getPosition()),
                () -> assertNotEquals(oldEnglish, actual.getEnglishLevel())
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(NullPointerException.class, () -> candidateProfileService.update(null),
                "Null pointer exception will be thrown, because we pass null user to method update.");

        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.update(new CandidateProfile()),
                "Entity not found exception will be thrown because we have no this candidate.");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 3L;
        candidateProfileService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.readById(id),
                "Entity not found exception will be thrown because we already delete this candidate.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.delete(0),
                "Entity not found exception will be thrown because we have no candidate with id 0.");
    }

    @Test
    public void test_GetAll() {
        assertFalse(candidateProfileService.getAll().isEmpty());
        CandidateProfile expected = candidateProfileService.readById(2L);

        assertTrue(candidateProfileService.getAll()
                .stream()
                .anyMatch(candidateProfile -> candidateProfile.equals(expected)));
    }
}
