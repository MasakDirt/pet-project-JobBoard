package com.board.job.service;

import com.board.job.model.entity.Messenger;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class MessengerServiceTests {
    private final MessengerService messengerService;
    private final VacancyService vacancyService;
    private final CandidateProfileService candidateProfileService;

    private List<Messenger> messengers;

    @Autowired
    public MessengerServiceTests(MessengerService messengerService, VacancyService vacancyService,
                                 CandidateProfileService candidateProfileService) {
        this.messengerService = messengerService;
        this.vacancyService = vacancyService;
        this.candidateProfileService = candidateProfileService;
    }

    @BeforeEach
    public void setMessengers() {
        messengers = messengerService.getAll();
    }

    @Test
    public void test_Injected_Components() {
        assertThat(messengerService).isNotNull();
        assertThat(vacancyService).isNotNull();
        assertThat(candidateProfileService).isNotNull();
        assertThat(messengers).isNotNull();
    }

    @Test
    public void test_GetAll() {
        assertFalse(messengerService.getAll().isEmpty(),
                "All messengers list must be not empty.");
        assertEquals(messengers, messengerService.getAll(),
                "Lists of all messengers must be the same");
    }

    @Test
    public void test_Valid_Create() {
        long vacancyId = 2L;
        long candidateProfileId = 1L;

        Messenger expected = Messenger.of(
                vacancyService.readById(vacancyId),
                candidateProfileService.readById(candidateProfileId)
        );

        Messenger actual = messengerService.create(vacancyId, candidateProfileId);
        expected.setId(actual.getId());

        assertTrue(messengers.size() < messengerService.getAll().size(),
                "Because of creating new messenger updated vacancy list must be bigger");
        assertEquals(expected, actual,
                "Created entity and expected must be the same, because they have same components");
    }

    @Test
    public void test_Invalid_Create() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.create(0, 3L),
                "Entity not found exception will be thrown because we have no vacancy with id 0.");
        assertThrows(EntityNotFoundException.class, () -> messengerService.create(2L,0),
                "Entity not found exception will be thrown because we have no candidate profile with id 0.");
    }

    @Test
    public void test_Valid_ReadById() {
        Messenger expected = messengerService.create(5L, 1L);

        Messenger actual = messengerService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_ReadById() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.readById(0),
                "Entity not found exception will be thrown because we have no messenger with id 0.");
    }

    @Test
    public void test_Valid_Delete() {
        long id = 2L;
        messengerService.delete(id);

        assertTrue(messengers.size() > messengerService.getAll().size(),
                "After deleting all messengers size that reads before must be bigger");
        assertThrows(EntityNotFoundException.class, () -> messengerService.readById(id),
                "Entity not found exception will be thrown because we delete messenger with id 2,so now we have no this.");
    }

    @Test
    public void test_Invalid_Delete() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.delete(0),
                "Entity not found exception will be thrown because we have no messenger with id 0.");
    }

    @Test
    public void test_Valid_GetAllByVacancyId() {
        long vacancyId = 7L;

        List<Messenger> expected = messengerService.getAll()
                .stream()
                .filter(messenger ->  messenger.getVacancy().getId() == vacancyId)
                .toList();

        List<Messenger> actual = messengerService.getAllByVacancyId(vacancyId);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_GetAllByVacancyId() {
     assertTrue(messengerService.getAllByVacancyId(0).isEmpty(),
             "We have no vacancy with id 0, so here must be empty list");
    }

    @Test
    public void test_Valid_GetAllByCandidateId() {
        long candidateId = 1L;

        List<Messenger> expected = messengerService.getAll()
                .stream()
                .filter(messenger ->  messenger.getCandidate().getId() == candidateId)
                .toList();

        List<Messenger> actual = messengerService.getAllByCandidateId(candidateId);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void test_Invalid_GetAllByCandidateId() {
     assertTrue(messengerService.getAllByCandidateId(0).isEmpty(),
             "We have no candidate with id 0, so here must be empty list");
    }
}
