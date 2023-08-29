package com.board.job.repository;

import com.board.job.model.entity.Vacancy;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class VacancyRepositoryTests {
    private final VacancyRepository vacancyRepository;

    @Autowired
    public VacancyRepositoryTests(VacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(vacancyRepository).isNotNull();
    }

    @Test
    public void test_Valid_GetVacanciesByEmployerCompanyId() {
        long employerCompanyId = 3L;
        List<Vacancy> expected = vacancyRepository.findAll()
                .stream()
                .filter(vacancy -> vacancy.getEmployerCompany().getId() == employerCompanyId)
                .toList();

        List<Vacancy> actual = vacancyRepository.getVacanciesByEmployerCompanyId(employerCompanyId);

        assertFalse(actual.isEmpty(),
                "We must get false because in this employer company vacancies are available.");
        assertTrue(vacancyRepository.findAll().size() > actual.size(),
                "Actual vacancies size must be smaller than all vacancies");
        assertEquals(expected, actual,
                "Expected and actual lists must be the same because it`s read by same employer company id");
    }

    @Test
    public void test_Invalid_GetVacanciesByEmployerCompanyId() {
        assertTrue(vacancyRepository.getVacanciesByEmployerCompanyId(0).isEmpty(),
                "We have no employer company with id 0, so here must be empty list!");
    }
}
