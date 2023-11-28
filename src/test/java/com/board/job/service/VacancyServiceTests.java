package com.board.job.service;

import com.board.job.exception.UserIsNotEmployer;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.entity.sample.LanguageLevel;
import com.board.job.model.entity.sample.WorkMode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
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
public class VacancyServiceTests {
    private final UserService userService;
    private final VacancyService vacancyService;

    private List<Vacancy> vacancies;

    @Autowired
    public VacancyServiceTests(UserService userService, VacancyService vacancyService) {
        this.userService = userService;
        this.vacancyService = vacancyService;
    }

    @BeforeEach
    public void setVacancies() {
        vacancies = vacancyService.getAll();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(userService).isNotNull();
        assertThat(vacancyService).isNotNull();
    }

    @Test
    public void test_Valid_GetAll() {
        assertFalse(vacancyService.getAll().isEmpty(),
                "All vacancies list must be not empty.");
        assertEquals(vacancies, vacancyService.getAll(),
                "Lists of all vacancies must be the same");
    }

    @Test
    public void test_Valid_Create() {
        long ownerId = 2L;

        Vacancy expected = new Vacancy();
        expected.setLookingFor("Someone");
        expected.setDomain(JobDomain.DATING);
        expected.setDetailDescription("Company of...");
        expected.setCategory(Category.GO);
        expected.setWorkMode(WorkMode.ONLY_OFFICE);
        expected.setCountry("Poland");
        expected.setSalaryFrom(200);
        expected.setSalaryTo(1000);
        expected.setWorkExperience(0.5);
        expected.setEnglishLevel(LanguageLevel.PRE_INTERMEDIATE);

        Vacancy actual = vacancyService.create(ownerId, expected);
        expected.setId(actual.getId());

        assertTrue(vacancies.size() < vacancyService.getAll().size(),
                "Because of creating new entity updated vacancy list must be bigger");

        assertEquals(expected, actual,
                "Vacancy that creates must be equal to expected");
    }

    @Test
    public void test_Invalid_Create() {
        Vacancy vacancy = vacancyService.readById(2L);
        long ownerId = 1L;

        assertAll(
                () -> assertThrows(EntityNotFoundException.class, () -> vacancyService.create(0, vacancy),
                        "Entity not found exception will be thrown because we have no user with id 0."),

                () -> assertThrows(UserIsNotEmployer.class, () -> vacancyService.create(3L, vacancy),
                        "User is not employer exception will be thrown because user have no employer profiles."),

                () -> assertThrows(NullPointerException.class, () -> vacancyService.create(ownerId, null),
                        "Null pointer exception will be thrown because we pass in create method null vacancy"),

                () -> assertThrows(ConstraintViolationException.class, () -> vacancyService.create(ownerId, new Vacancy()),
                        "Constraint violation exception will be thrown because we have pass in create method " +
                                "invalid vacancy.")
        );
    }

    @Test
    public void test_Valid_ReadById() {
        long id = 4L;

        Vacancy expected = vacancies
                .stream()
                .filter(vacancy -> vacancy.getId() == id)
                .findFirst()
                .orElse(new Vacancy());

        Vacancy actual = vacancyService.readById(id);

        assertEquals(expected, actual,
                "Vacancies must be same, because they read by same id.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> vacancyService.readById(0),
                "Entity not found exception will be thrown because we have no vacancy with id 0.");
    }

    @Test
    public void test_Valid_Update() {
        long vacancyId = 3L;
        String newLookingFor = "Look for Python..";

        Vacancy unexpected = vacancyService.readById(vacancyId);
        String oldLookingFor = unexpected.getLookingFor();
        String oldDetailDescr = unexpected.getDetailDescription();
        String oldCountry = unexpected.getCountry();
        double oldWorkExperience = unexpected.getWorkExperience();

        unexpected.setLookingFor(newLookingFor);

        Vacancy actual = vacancyService.update(unexpected.getId(), unexpected);

        assertAll(
                () -> assertEquals(vacancyId, actual.getId(),
                        "After updating id`s must be sames because we do not update it."),

                () -> assertEquals(oldDetailDescr, actual.getDetailDescription(),
                        "After updating detail description`s must be sames because we do not update it."),

                () -> assertEquals(oldCountry, actual.getCountry(),
                        "After updating countries must be sames because we do not update it."),

                () -> assertEquals(oldWorkExperience, actual.getWorkExperience(),
                        "After updating work experiences must be sames because we do not update it."),

                () -> assertNotEquals(oldLookingFor, actual.getLookingFor(),
                        "After updating old looking for must be different because we update it."),

                () -> assertEquals(newLookingFor, actual.getLookingFor(),
                        "After updating new looking for must be same because we update it.")
        );
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(NullPointerException.class, () -> vacancyService.update(2L, null),
                "Null pointer exception will be thrown because we pass in update method null vacancy");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 5L;

        vacancyService.delete(id);

        assertTrue(vacancies.size() > vacancyService.getAll().size(),
                "After deleting all vacancies size that reads before must be bigger");
        assertThrows(EntityNotFoundException.class, () -> vacancyService.readById(id),
                "Entity not found exception will be thrown because we delete vacancy with id 5,so now we have no this.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> vacancyService.delete(0),
                "Entity not found exception will be thrown because we have no vacancy with id 0.");
    }

    @Test
    public void test_GetSorted() {
        Sort sort = Sort.by(Sort.Direction.fromString("desc"), "postedAt");
        Pageable pageable = PageRequest.of(0, 10, sort);

        List<Vacancy> expected = vacancyService.getAll()
                .stream()
                .sorted(((o1, o2) -> o2.getPostedAt().compareTo(o1.getPostedAt())))
                .toList();

        List<Vacancy> actual = vacancyService.getSortedVacancies(pageable, "")
                .stream()
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    public void test_Valid_GetAllByEmployerProfileId() {
        long employerId = 3L;

        List<Vacancy> expected = vacancyService.getAll()
                .stream()
                .filter(vacancy -> vacancy.getEmployerProfile().getId() == employerId)
                .toList();

        List<Vacancy> actual = vacancyService.getAllByEmployerProfileId(employerId);

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_GetAllByEmployerProfileId() {
        assertTrue(vacancyService.getAllByEmployerProfileId(0).isEmpty(),
                "Here must be empty list because we have no employer with id 0.");
    }
}
