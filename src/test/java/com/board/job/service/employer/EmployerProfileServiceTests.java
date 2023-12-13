package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.employer.EmployerProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
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
public class EmployerProfileServiceTests {
    private final EmployerProfileService employerProfileService;
    private final EmployerProfileRepository employerProfileRepository;

    List<EmployerProfile> employerProfiles;

    @Autowired
    public EmployerProfileServiceTests(EmployerProfileService employerProfileService, EmployerProfileRepository employerProfileRepository) {
        this.employerProfileService = employerProfileService;
        this.employerProfileRepository = employerProfileRepository;
    }

    @BeforeEach
    public void setEmployerProfiles() {
        employerProfiles = employerProfileRepository.findAll();
    }

    @Test
    public void testInjectedComponents() {
        AssertionsForClassTypes.assertThat(employerProfileService).isNotNull();
        AssertionsForClassTypes.assertThat(employerProfileRepository).isNotNull();
        AssertionsForClassTypes.assertThat(employerProfiles).isNotNull();
    }

    @Test
    public void testValidCreate() {
        long ownerId = 4L;

        EmployerProfile expected = new EmployerProfile();
        expected.setLinkedInProfile("www.linkedin.co/orjkgoirt");
        expected.setPositionInCompany("User");
        expected.setCompanyName("Name of company");
        expected.setTelegram("@telegram");
        expected.setPhone("phone-4503506");

        EmployerProfile actual = employerProfileService.create(ownerId, expected);
        expected.setId(actual.getId());

        assertTrue(employerProfiles.size() < employerProfileRepository.findAll().size(),
                "After creating size of list that reads before must be smaller.");
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidCreate() {
        assertThrows(ConstraintViolationException.class, () -> employerProfileService.create(5, new EmployerProfile()),
                "Constraint violation exception will be thrown because we pass invalid employer company in method");
    }

    @Test
    public void testValidReadById() {
        EmployerProfile expected = new EmployerProfile();
        expected.setLinkedInProfile("www.linkedin.co/orjkgoirt");
        expected.setPositionInCompany("User");
        expected.setCompanyName("Name of company");
        expected.setTelegram("@telegram");
        expected.setPhone("phone-4503506");

        expected = employerProfileService.create(3L, expected);

        EmployerProfile actual = employerProfileService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidReadById() {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.readById(0),
                "Entity not found exception will be thrown because we have no this employer profile!");
    }

    @Test
    public void testValidUpdate() {
        EmployerProfile unexpected = employerProfileService.readById(2L);
        String oldCompanyName = unexpected.getCompanyName();
        String telegram = unexpected.getTelegram();
        String oldPositionInCompany = unexpected.getPositionInCompany();

        unexpected.setCompanyName("Company");
        unexpected.setPositionInCompany("new pos");

        EmployerProfile actual = employerProfileService.update(unexpected.getId(), unexpected);

        assertEquals(unexpected.getId(), actual.getId(),
                "After updating, id`s must be equal.");

        assertNotEquals(oldCompanyName, actual.getCompanyName(),
                "We update company name so it`s should be different.");

        assertNotEquals(oldPositionInCompany, actual.getPositionInCompany(),
                "We update position in company so it`s should be different.");

        assertEquals(telegram, actual.getTelegram(),
                "Telegram wo don`t update, so it`s must be sames");
    }

    @Test
    public void testInvalidUpdate() {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.update(0, new EmployerProfile()),
                "Entity not found exception will be thrown because we have no this in db.");
    }

    @Test
    public void testValidDelete() {
        long id = 2L;
        employerProfileService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> employerProfileService.readById(id),
                "Entity not found exception will be thrown because we already delete it.");
    }

    @Test
    public void testInvalidDelete() {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.delete(0),
                "Entity not found exception will be thrown because we have no employer profile with id 0.");
    }
}
