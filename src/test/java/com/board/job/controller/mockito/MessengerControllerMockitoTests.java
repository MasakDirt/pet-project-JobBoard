package com.board.job.controller.mockito;

import com.board.job.controller.MessengerController;
import com.board.job.model.dto.messenger.CutMessengerResponse;
import com.board.job.model.entity.Messenger;
import com.board.job.model.entity.User;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.mapper.MessengerMapper;
import com.board.job.service.MessengerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class MessengerControllerMockitoTests {
    @InjectMocks
    private MessengerController controller;
    @Mock
    private MessengerService service;
    @Mock
    private MessengerMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_GetAllCandidateMessengers() {
        long ownerId = 4L;
        long candidateId = 2L;
        Messenger messenger = new Messenger();
        List<Messenger> cutMessengerResponses = List.of(new Messenger(), new Messenger());

        when(service.getAllByCandidateId(candidateId)).thenReturn(cutMessengerResponses);
        when(mapper.getCutMessengerResponseFromMessenger(messenger)).thenReturn(CutMessengerResponse.builder().build());
        controller.getAllCandidateMessengers(ownerId, candidateId, authentication);

        verify(service, times(1)).getAllByCandidateId(candidateId);
        verify(mapper, times(cutMessengerResponses.size())).getCutMessengerResponseFromMessenger(messenger);
    }

    @Test
    public void test_NotFound_GetAllCandidateMessengers() {
        long ownerId = 5L;
        long candidateId = 0L;
        when(service.getAllByCandidateId(candidateId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getAllCandidateMessengers(ownerId, candidateId, authentication));

        verify(service, times(1)).getAllByCandidateId(candidateId);
    }

    @Test
    public void test_GetAllVacancyMessengers() {
        long ownerId = 4L;
        long employerId = 2L;
        long vacancyId = 3L;

        Messenger messenger = new Messenger();
        List<Messenger> cutMessengerResponses = List.of(new Messenger(), new Messenger(), new Messenger());

        when(service.getAllByVacancyId(vacancyId)).thenReturn(cutMessengerResponses);
        when(mapper.getCutMessengerResponseFromMessenger(messenger)).thenReturn(CutMessengerResponse.builder().build());
        controller.getAllVacancyMessengers(ownerId, employerId, vacancyId, authentication);

        verify(service, times(1)).getAllByVacancyId(vacancyId);
        verify(mapper, times(cutMessengerResponses.size())).getCutMessengerResponseFromMessenger(messenger);
    }

    @Test
    public void test_NotFound_GetAllVacancyMessengers() {
        long ownerId = 4L;
        long employerId = 2L;
        long vacancyId = 0L;

        when(service.getAllByVacancyId(vacancyId)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getAllVacancyMessengers(
                ownerId, employerId, vacancyId, authentication));

        verify(service, times(1)).getAllByVacancyId(vacancyId);
    }

    @Test
    public void test_DeleteByCandidate() {
        long ownerId = 4L;
        long candidateId = 2L;
        long id = 6L;
        User owner = new User();
        owner.setFirstName("New");
        owner.setLastName("Name");
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setOwner(owner);

        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setCompanyName("New Company");
        Vacancy vacancy = new Vacancy();
        vacancy.setEmployerProfile(employerProfile);

        Messenger messenger = new Messenger();
        messenger.setCandidate(candidateProfile);
        messenger.setVacancy(vacancy);

        when(service.readById(id)).thenReturn(messenger);
        controller.deleteByCandidate(ownerId, candidateId, id, authentication);

        verify(service, times(1)).delete(id);
    }

    @Test
    public void test_DeleteByEmployer() {
        long ownerId = 4L;
        long employerId = 2L;
        long vacancyId = 4L;
        long id = 6L;
        User owner = new User();
        owner.setFirstName("New");
        owner.setLastName("Name");
        CandidateProfile candidateProfile = new CandidateProfile();
        candidateProfile.setOwner(owner);

        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setCompanyName("New Company");
        Vacancy vacancy = new Vacancy();
        vacancy.setEmployerProfile(employerProfile);

        Messenger messenger = new Messenger();
        messenger.setCandidate(candidateProfile);
        messenger.setVacancy(vacancy);

        when(service.readById(id)).thenReturn(messenger);
        controller.deleteByEmployer(ownerId, employerId, vacancyId, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
