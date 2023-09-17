package com.board.job.service.mockito;

import com.board.job.model.entity.Messenger;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.repository.MessengerRepository;
import com.board.job.service.MessengerService;
import com.board.job.service.VacancyService;
import com.board.job.service.candidate.CandidateProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class MessengerServiceMockitoTests {
    @InjectMocks
    private MessengerService messengerService;
    @Mock
    private MessengerRepository messengerRepository;
    @Mock
    private VacancyService vacancyService;
    @Mock
    private CandidateProfileService candidateProfileService;

    @Test
    public void test_Create() {
        long vacancyId = 2L;
        long candidateProfileId = 3L;

        when(vacancyService.readById(vacancyId)).thenReturn(new Vacancy());
        when(candidateProfileService.readById(candidateProfileId)).thenReturn(new CandidateProfile());
        messengerService.create(vacancyId, candidateProfileId);

        verify(messengerRepository, times(1)).save(new Messenger());
    }

    @Test
    public void test_ReadById() {
        long id = 1L;
        Messenger messenger = new Messenger();
        messenger.setCandidate(new CandidateProfile());

        when(messengerRepository.findById(id)).thenReturn(Optional.of(messenger));
        Messenger actual = messengerService.readById(id);

        verify(messengerRepository, times(1)).findById(id);

        assertEquals(messenger, actual);
    }

    @Test
    public void test_Delete() {
        long id = 4L;

        when(messengerRepository.findById(id)).thenReturn(Optional.of(new Messenger()));
        messengerService.delete(id);

        verify(messengerRepository, times(1)).findById(id);
    }

    @Test
    public void test_GetAll() {
        List<Messenger> expected = List.of(new Messenger(), new Messenger());
        when(messengerRepository.findAll()).thenReturn(expected);
        List<Messenger> actual = messengerService.getAll();

        verify(messengerRepository, times(1)).findAll();

        assertEquals(expected, actual);
    }
}
