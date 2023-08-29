package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.repository.employer.EmployerCompanyRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class EmployerCompanyServiceTests {
    private final UserService userService;
    private final EmployerCompanyService employerCompanyService;
    private final EmployerCompanyRepository employerCompanyRepository;

    private List<EmployerCompany> employerCompanies;

    @Autowired
    public EmployerCompanyServiceTests(UserService userService, EmployerCompanyService employerCompanyService, EmployerCompanyRepository employerCompanyRepository) {
        this.userService = userService;
        this.employerCompanyService = employerCompanyService;
        this.employerCompanyRepository = employerCompanyRepository;
    }

    @BeforeEach
    public void setEmployerCompanies() {
        employerCompanies = employerCompanyRepository.findAll();
    }

    @Test
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(userService).isNotNull();
        AssertionsForClassTypes.assertThat(employerCompanyService).isNotNull();
        AssertionsForClassTypes.assertThat(employerCompanyRepository).isNotNull();
        AssertionsForClassTypes.assertThat(employerCompanies).isNotNull();
    }

    @Test
    public void test_Valid_Create() {
        long ownerId = 3L;
        EmployerCompany expected = new EmployerCompany();
        expected.setAboutCompany("About");
        expected.setWebSite("website");

        EmployerCompany actual = employerCompanyService.create(ownerId, expected);
        expected.setId(actual.getId());
        expected.setOwner(userService.readById(ownerId));

        assertTrue(employerCompanies.size() < employerCompanyRepository.findAll().size(),
                "Before creating list must be smaller.");

        assertEquals(expected, actual,
                "it`s created with same options, so they must be equal.");
    }

    @Test
    public void test_Invalid_Create() {
        assertThrows(EntityNotFoundException.class,() -> employerCompanyService.create(0, new EmployerCompany()),
                "Entity not found exception will be thrown because we have no this user!");

        assertThrows(NullPointerException.class,() -> employerCompanyService.create(3, null),
                "Null pointer exception will be thrown because we pass null employer company value!");

        assertThrows(DataIntegrityViolationException.class,() -> employerCompanyService.create(3, new EmployerCompany()),
                "Data integrity violation exception will be thrown because we pass not appropriate employer company value!");
    }

    @Test
    public void test_Valid_ReadById() {
        EmployerCompany expected = new EmployerCompany();
        expected.setAboutCompany("About");
        expected.setWebSite("website");

        expected = employerCompanyService.create(4L, expected);

        EmployerCompany actual = employerCompanyService.readById(expected.getId());

        assertEquals(expected, actual,
                "They reads by one id, so they must be equal.");
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class,() -> employerCompanyService.readById(0),
                "Entity not found exception will be thrown because we have no this employer company!");
    }

    @Test
    public void test_Valid_Update() {
        String newWeb = "newWEb.co";

        EmployerCompany unexpected = employerCompanyService.readById(3);
        String webSite = unexpected.getWebSite();
        String aboutCompany = unexpected.getAboutCompany();

        unexpected.setWebSite(newWeb);
        EmployerCompany actual = employerCompanyService.update(unexpected);

        assertEquals(unexpected.getId(), actual.getId());
        assertNotEquals(webSite, actual.getWebSite());
        assertEquals(aboutCompany, actual.getAboutCompany());
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(NullPointerException.class,() -> employerCompanyService.update(null),
                "Null pointer exception will be thrown because we pass null employer company value!");

        assertThrows(EntityNotFoundException.class,() -> employerCompanyService.update(new EmployerCompany()),
                "Entity not found exception will be thrown because we have no this employer company!");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 2L;
        employerCompanyService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> employerCompanyService.readById(id),
                "We already delete this company, wo here must be thrown an entity not found exception.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> employerCompanyService.delete(0),
                "Entity not found exception will be thrown because we have no this employer company!");
    }
}
