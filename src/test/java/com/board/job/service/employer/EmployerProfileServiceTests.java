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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
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
    public void test_Injected_Components() {
        AssertionsForClassTypes.assertThat(employerProfileService).isNotNull();
        AssertionsForClassTypes.assertThat(employerProfileRepository).isNotNull();
        AssertionsForClassTypes.assertThat(employerProfiles).isNotNull();
    }

    @Test
    public void test_Valid_Create() {
        String fileName = "files/photos/nicolas.jpg";
        long ownerId = 4L;

        EmployerProfile expected = new EmployerProfile();
        expected.setLinkedInProfile("www.linkedin.co/orjkgoirt");
        expected.setPositionInCompany("User");
        expected.setCompanyName("Name of company");
        expected.setTelegram("@telegram");
        expected.setPhone("phone-4503506");

        EmployerProfile actual = employerProfileService.create(fileName, ownerId, expected);
        expected.setId(actual.getId());

        assertTrue(employerProfiles.size() < employerProfileRepository.findAll().size(),
                "After creating size of list that reads before must be smaller.");
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_Create() {
        assertThrows(ConstraintViolationException.class, () -> employerProfileService.create(
                        "files/photos/violetPhoto.jpg", 5, new EmployerProfile()),
                "Constraint violation exception will be thrown because we pass invalid employer company in method");
        assertThrows(NullPointerException.class, () -> employerProfileService.create(
                        "files/photos/violetPhoto.jpg", 3, null),
                "Null pointer exception will be thrown because we pass null employer company in method");
    }

    @Test
    public void test_Valid_ReadById() {
        EmployerProfile expected = new EmployerProfile();
        expected.setLinkedInProfile("www.linkedin.co/orjkgoirt");
        expected.setPositionInCompany("User");
        expected.setCompanyName("Name of company");
        expected.setTelegram("@telegram");
        expected.setPhone("phone-4503506");

        expected = employerProfileService.create("files/photos/adminPhoto.jpg", 3L, expected);

        EmployerProfile actual = employerProfileService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.readById(0),
                "Entity not found exception will be thrown because we have no this employer profile!");
    }

    @Test
    public void test_Valid_Update() {
        EmployerProfile unexpected = employerProfileService.readById(2L);
        String oldCompanyName = unexpected.getCompanyName();
        String telegram = unexpected.getTelegram();
        String oldPositionInCompany = unexpected.getPositionInCompany();

        unexpected.setCompanyName("Company");
        unexpected.setPositionInCompany("new pos");

        EmployerProfile actual = employerProfileService.update(unexpected);

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
    public void test_Invalid_Update() {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.update(new EmployerProfile()),
                "Entity not found exception will be thrown because we have no this in db.");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 2L;
        employerProfileService.delete(id);

        assertThrows(EntityNotFoundException.class, () -> employerProfileService.readById(id),
                "Entity not found exception will be thrown because we already delete it.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.delete(0),
                "Entity not found exception will be thrown because we have no employer profile with id 0.");
    }

    @Test
    public void test_Valid_GetWithPropertyPictureAttachedById() {
        long id = 3L;
        byte[] expected = employerProfileRepository.findWithPropertyPictureAttachedById(id);

        byte[] actual = employerProfileService.getWithPropertyPictureAttachedById(id);

        assertEquals(Arrays.toString(expected), Arrays.toString(actual),
                "We read bytes be same id, so they must be equal.");
    }

    @Test
    public void test_Invalid_GetWithPropertyPictureAttachedById() {
        assertEquals(Arrays.toString(new byte[]{}), Arrays.toString(employerProfileService.getWithPropertyPictureAttachedById(0)),
                "Arrays must be empty because we hve no user with this id.");
    }

    @Test
    public void test_Valid_SaveProfilePicture() throws Exception {
        long id = 2L;
        EmployerProfile employerProfile = employerProfileService.readById(id);
        byte[] unexpected = employerProfile.getProfilePicture();

        employerProfileService.saveProfilePicture(id, Files.readAllBytes(Path.of("files/photos/nicolas.jpg")));

        byte[] actual = employerProfile.getProfilePicture();

        assertNotEquals(Arrays.toString(unexpected), Arrays.toString(actual),
                "We added new profile picture so 'unexpected' array must be empty");
    }

    @Test
    public void test_Invalid_SaveProfilePicture() throws Exception {
        assertThrows(EntityNotFoundException.class, () -> employerProfileService.saveProfilePicture(
                0, Files.readAllBytes(Path.of("files/photos/nicolas.jpg"))),
        "Entity not found exception will be thrown because we have no employer profile with id 0.");
    }
}
