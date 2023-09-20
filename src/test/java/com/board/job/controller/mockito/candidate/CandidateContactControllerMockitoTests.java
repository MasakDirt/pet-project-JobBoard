package com.board.job.controller.mockito.candidate;

import com.board.job.controller.candidate.CandidateContactController;
import com.board.job.model.dto.candidate_contact.CandidateContactRequest;
import com.board.job.model.dto.candidate_contact.CandidateContactResponse;
import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.model.mapper.candidate.CandidateContactMapper;
import com.board.job.service.candidate.CandidateContactService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CandidateContactControllerMockitoTests {
    @InjectMocks
    private CandidateContactController candidateContactController;
    @Mock
    private CandidateContactService candidateContactService;
    @Mock
    private CandidateContactMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_getCandidateById() {
        long id = 3L;
        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setId(id);
        candidateContact.setEmail("email");
        candidateContact.setTelegram("new telega");

        when(mapper.getCandidateContactResponseFromCandidateContact(candidateContact)).thenReturn(CandidateContactResponse.builder().build());
        when(candidateContactService.readById(id)).thenReturn(candidateContact);
        candidateContactController.getCandidateById(3L, id, authentication);

        verify(candidateContactService, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_getCandidateById() {
        long id = 100L;
        when(candidateContactService.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> candidateContactController.getCandidateById(12, id, authentication));

        verify(candidateContactService, times(1)).readById(id);
        verify(mapper, times(0)).getCandidateContactResponseFromCandidateContact(new CandidateContact());
    }

    @Test
    public void test_Create() {
        long ownerId = 3L;
        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setId(2L);
        candidateContact.setEmail("email");
        candidateContact.setTelegram("new telega");

        when(mapper.getCandidateContactFromCandidateContactRequest(CandidateContactRequest.builder().build())).thenReturn(candidateContact);
        when(candidateContactService.create(ownerId, candidateContact)).thenReturn(candidateContact);
        candidateContactController.create(ownerId, CandidateContactRequest.builder().build(), authentication);

        verify(candidateContactService, times(1)).create(ownerId, candidateContact);
    }

    @Test
    public void test_Update() {
        long ownerId = 3L;
        long id = 3L;
        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setId(id);
        candidateContact.setEmail("email");
        candidateContact.setTelegram("new telega");

        when(mapper.getCandidateContactFromCandidateContactRequest(CandidateContactRequest.builder().build())).thenReturn(candidateContact);
        when(candidateContactService.update(id, candidateContact)).thenReturn(candidateContact);
        candidateContactController.update(ownerId, id,
                CandidateContactRequest.builder().build(), authentication);

        verify(candidateContactService, times(1)).update(id, candidateContact);
    }

    @Test
    public void test_Delete() {
        long ownerId = 3L;
        long id = 3L;
        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setId(id);
        candidateContact.setEmail("email");
        candidateContact.setTelegram("new telega");

        when(candidateContactService.readById(id)).thenReturn(candidateContact);
        candidateContactController.delete(ownerId, id, authentication);

        verify(candidateContactService, times(1)).delete(id);
    }
}
