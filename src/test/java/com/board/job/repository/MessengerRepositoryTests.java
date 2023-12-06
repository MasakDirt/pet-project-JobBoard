package com.board.job.repository;

import com.board.job.model.entity.Messenger;
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
public class MessengerRepositoryTests {
    private final MessengerRepository messengerRepository;

    @Autowired
    public MessengerRepositoryTests(MessengerRepository messengerRepository) {
        this.messengerRepository = messengerRepository;
    }

    @Test
    public void test_Injected_Component() {
        AssertionsForClassTypes.assertThat(messengerRepository).isNotNull();
    }

    @Test
    public void test_Valid_FindAllByCandidateId() {
        long candidateId = 2L;
        List<Messenger> expected = messengerRepository.findAll()
                .stream()
                .filter(messenger -> messenger.getCandidateProfile().getId() == candidateId)
                .toList();

        List<Messenger> actual = messengerRepository.findAllByCandidateProfileId(candidateId);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_FindAllByCandidateId() {
        assertTrue(messengerRepository.findAllByCandidateProfileId(0).isEmpty(),
                "we have no candidate with id 0, so here must be empty list.");
    }

    @Test
    public void test_Valid_FindAllByVacancyId() {
        long vacancyId = 8L;
        List<Messenger> expected = messengerRepository.findAll()
                .stream()
                .filter(messenger -> messenger.getVacancy().getId() == vacancyId)
                .toList();

        List<Messenger> actual = messengerRepository.findAllByVacancyId(vacancyId);

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_FindAllByVacancyId() {
        assertTrue(messengerRepository.findAllByVacancyId(0).isEmpty(),
                "We have no vacancy with id 0, so here must be empty list.");
    }
}
