package com.board.job.repository;

import com.board.job.repository.employer.EmployerProfileRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class EmployerProfileRepositoryTests {
    private final EmployerProfileRepository employerProfileRepository;

    @Autowired
    public EmployerProfileRepositoryTests(EmployerProfileRepository employerProfileRepository) {
        this.employerProfileRepository = employerProfileRepository;
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(employerProfileRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindWithPropertyPictureAttachedById() {
        long id = 3L;

        byte[] expected = employerProfileRepository.findAll()
                .stream()
                .filter(employerProfile -> employerProfile.getId() == id)
                .findFirst()
                .get()
                .getProfilePicture();

        byte[] actual = employerProfileRepository.findWithPropertyPictureAttachedById(id);

        assertNotEquals(0, actual.length,
                "We must get false because we have candidate contact with that id.");
        assertEquals(expected.length, actual.length,
                "Profile picture that read by same id, must be sames");
    }

    @Test
    public void test_Invalid_FindWithPropertyPictureAttachedById() {
        assertNull(employerProfileRepository.findWithPropertyPictureAttachedById(0),
                "Array must be null because we have no employer profile with 0 id.");
    }
}
