package com.board.job.repository;

import com.board.job.repository.candidate.CandidateContactsRepository;
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
public class CandidateContactsRepositoryTests {
    private final CandidateContactsRepository candidateContactsRepository;

    @Autowired
    public CandidateContactsRepositoryTests(CandidateContactsRepository candidateContactsRepository) {
        this.candidateContactsRepository = candidateContactsRepository;
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(candidateContactsRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindWithPropertyPictureAttachedById() {
        long id = 3L;

        byte[] expected = candidateContactsRepository.findAll()
                .stream()
                .filter(candidateContacts -> candidateContacts.getId() == id)
                .findFirst()
                .get()
                .getProfilePicture();

        byte[] actual = candidateContactsRepository.findWithPropertyPictureAttachedById(id);

        assertNotEquals(0, actual.length,
                "We must get false because we have candidate contact with that id.");
        assertEquals(expected.length, actual.length,
                "Profile picture that read by same id, must be sames");
    }

    @Test
    public void test_Invalid_FindWithPropertyPictureAttachedById() {
        assertNull(candidateContactsRepository.findWithPropertyPictureAttachedById(0),
                "Array must be null because we have no candidate contacts with 0 id.");
    }
}
