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

    private List<Messenger> messengerForVacanciesReplies;

    @Autowired
    public MessengerServiceTests(MessengerService messengerService, VacancyService vacancyService,
                                 CandidateProfileService candidateProfileService) {
        this.messengerService = messengerService;
        this.vacancyService = vacancyService;
        this.candidateProfileService = candidateProfileService;
    }

    @BeforeEach
    public void setMessengers() {
        messengerForVacanciesReplies = messengerService.getAll();
    }

    @Test
    public void testInjectedComponents() {
        assertThat(messengerService).isNotNull();
        assertThat(vacancyService).isNotNull();
        assertThat(candidateProfileService).isNotNull();
        assertThat(messengerForVacanciesReplies).isNotNull();
    }

    @Test
    public void testGetAll() {
        assertFalse(messengerService.getAll().isEmpty(),
                "All messengers list must be not empty.");
        assertEquals(messengerForVacanciesReplies, messengerService.getAll(),
                "Lists of all messengers must be the same");
    }

    @Test
    public void testValidCreate() {
        long vacancyId = 2L;
        long candidateProfileId = 1L;

        Messenger expected = Messenger.of(
                vacancyService.readById(vacancyId),
                candidateProfileService.readById(candidateProfileId)
        );

        Messenger actual = messengerService.create(vacancyId, candidateProfileId);
        expected.setId(actual.getId());

        assertTrue(messengerForVacanciesReplies.size() < messengerService.getAll().size(),
                "Because of creating new messenger updated vacancy list must be bigger");
        assertEquals(expected, actual,
                "Created entity and expected must be the same, because they have same components");
    }

    @Test
    public void testInvalidCreate() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.create(0, 3L),
                "Entity not found exception will be thrown because we have no vacancy with id 0.");
        assertThrows(EntityNotFoundException.class, () -> messengerService.create(2L,0),
                "Entity not found exception will be thrown because we have no candidate profile with id 0.");
    }

    @Test
    public void testValidReadById() {
        Messenger expected = messengerService.create(5L, 1L);

        Messenger actual = messengerService.readById(expected.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidReadById() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.readById(0),
                "Entity not found exception will be thrown because we have no messenger with id 0.");
    }

    @Test
    public void testValidDelete() {
        long id = 2L;
        messengerService.delete(id);

        assertTrue(messengerForVacanciesReplies.size() > messengerService.getAll().size(),
                "After deleting all messengers size that reads before must be bigger");
        assertThrows(EntityNotFoundException.class, () -> messengerService.readById(id),
                "Entity not found exception will be thrown because we delete messenger with id 2,so now we have no this.");
    }

    @Test
    public void testInvalidDelete() {
        assertThrows(EntityNotFoundException.class, () -> messengerService.delete(0),
                "Entity not found exception will be thrown because we have no messenger with id 0.");
    }

    @Test
    public void testValidGetAllByVacancyId() {
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
    public void testInvalidGetAllByVacancyId() {
     assertTrue(messengerService.getAllByVacancyId(0).isEmpty(),
             "We have no vacancy with id 0, so here must be empty list");
    }

    @Test
    public void testValidGetAllByCandidateId() {
        long candidateId = 1L;

        List<Messenger> expected = messengerService.getAll()
                .stream()
                .filter(messenger ->  messenger.getCandidateProfile().getId() == candidateId)
                .toList();

        List<Messenger> actual = messengerService.getAllByCandidateProfileId(candidateId);

        assertFalse(actual.isEmpty());
        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidGetAllByCandidateId() {
     assertTrue(messengerService.getAllByCandidateProfileId(0).isEmpty(),
             "We have no candidate with id 0, so here must be empty list");
    }
}
