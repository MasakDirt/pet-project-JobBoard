package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.repository.candidate.CandidateProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final CandidateProfileRepository candidateProfileRepository;

    @Autowired
    public CandidateProfileServiceTests(CandidateProfileService candidateProfileService, CandidateProfileRepository candidateProfileRepository) {
        this.candidateProfileService = candidateProfileService;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    @Test
    public void testInjectedComponents() {
        assertThat(candidateProfileService).isNotNull();
    }

    @Test
    public void testValidCreate() {
        long ownerId = 2L;

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
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidCreate() {
        long ownerId = 2L;
        assertThrows(NullPointerException.class, () -> candidateProfileService.create(ownerId, null),
                "Null pointer exception will be thrown, because we pass null user to method create.");

        assertThrows(ConstraintViolationException.class, () -> candidateProfileService.create(ownerId, new CandidateProfile()),
                "Constraint violation exception will be thrown, because we pass empty user.");
    }

    @Test
    public void testValidReadById() {
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
    public void testInvalidReadById() {
        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.readById(0),
                "Entity not found exception will be thrown because we have no candidate with id 0.");
    }

    @Test
    public void testValidUpdate() {
        CandidateProfile unexpected = candidateProfileService.readById(1L);

        Category oldCategory = unexpected.getCategory();
        String oldPosition = unexpected.getPosition();
        LanguageLevel oldEnglish = unexpected.getEnglishLevel();

        unexpected.setPosition("New");
        unexpected.setCategory(Category.KOTLIN);
        unexpected.setEnglishLevel(LanguageLevel.ADVANCED_FLUENT);

        CandidateProfile actual = candidateProfileService.update(unexpected.getId(), unexpected);

        assertAll(
                () -> assertEquals(unexpected.getId(), actual.getId()),
                () -> assertNotEquals(oldCategory, actual.getCategory()),
                () -> assertNotEquals(oldPosition, actual.getPosition()),
                () -> assertNotEquals(oldEnglish, actual.getEnglishLevel())
        );
    }

    @Test
    public void testInvalidUpdate() {
        assertThrows(NullPointerException.class, () -> candidateProfileService.update(1, null),
                "Null pointer exception will be thrown, because we pass null user to method update.");
    }

    @Test
    public void testValidDelete() {
        long id = 3L;
        candidateProfileService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.readById(id),
                "Entity not found exception will be thrown because we already delete this candidate.");
    }

    @Test
    public void testInvalidDelete() {
        assertThrows(EntityNotFoundException.class, () -> candidateProfileService.delete(0),
                "Entity not found exception will be thrown because we have no candidate with id 0.");
    }

    @Test
    public void testGetAllSorted() {
        Sort sort = Sort.by(Sort.Direction.fromString("desc"), "position");
        Pageable pageable = PageRequest.of(0, 10, sort);

        List<CandidateProfile> expected = candidateProfileRepository.findAll()
                .stream()
                .sorted(((o1, o2) -> o2.getPosition().compareTo(o1.getPosition())))
                .toList();

        List<CandidateProfile> actual = candidateProfileService.getAllSorted(pageable, "")
                .stream()
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllSortedWithText() {
        Sort sort = Sort.by(Sort.Direction.fromString("desc"), "id");
        Pageable pageable = PageRequest.of(0, 10, sort);
        String text = "Java";

        List<CandidateProfile> expected = candidateProfileRepository.findAll()
                .stream()
                .filter(candidateProfile -> candidateProfile.getPosition().contains(text))
                .sorted(((o1, o2) -> o2.getPosition().compareTo(o1.getPosition())))
                .toList();

        List<CandidateProfile> actual = candidateProfileService.getAllSorted(pageable, text)
                .stream()
                .toList();

        assertEquals(expected, actual);
    }
}
