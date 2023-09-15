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
        String aboutCompany = "About";
        String webSite = "website";

        EmployerCompany expected = new EmployerCompany();
        expected.setAboutCompany(aboutCompany);
        expected.setWebSite(webSite);

        EmployerCompany actual = employerCompanyService.create(ownerId, aboutCompany, webSite);
        expected.setId(actual.getId());
        expected.setOwner(userService.readById(ownerId));

        assertTrue(employerCompanies.size() < employerCompanyRepository.findAll().size(),
                "Before creating list must be smaller.");

        assertEquals(expected, actual,
                "it`s created with same options, so they must be equal.");
    }

    @Test
    public void test_Invalid_Create() {
        String webSite = "website";

        assertThrows(EntityNotFoundException.class,() -> employerCompanyService.create(0, "", webSite),
                "Entity not found exception will be thrown because we have no this user!");
    }

    @Test
    public void test_Valid_ReadById() {
        String aboutCompany = "About";
        String webSite = "website";

        EmployerCompany expected = new EmployerCompany();
        expected.setAboutCompany(aboutCompany);
        expected.setWebSite(webSite);

        expected = employerCompanyService.create(4L, aboutCompany, webSite);

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
        EmployerCompany actual = employerCompanyService.update(unexpected.getId(), unexpected);

        assertEquals(unexpected.getId(), actual.getId());
        assertNotEquals(webSite, actual.getWebSite());
        assertEquals(aboutCompany, actual.getAboutCompany());
    }

    @Test
    public void test_Invalid_Update() {
        assertThrows(NullPointerException.class,() -> employerCompanyService.update(2, null),
                "Null pointer exception will be thrown because we pass null employer company value!");
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
