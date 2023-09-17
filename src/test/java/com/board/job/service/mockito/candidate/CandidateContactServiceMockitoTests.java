package com.board.job.service.mockito.candidate;

import com.board.job.model.entity.User;
import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.repository.candidate.CandidateContactRepository;
import com.board.job.service.UserService;
import com.board.job.service.candidate.CandidateContactService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class CandidateContactServiceMockitoTests {
    @InjectMocks
    private CandidateContactService candidateContactService;
    @Mock
    private CandidateContactRepository candidateContactRepository;
    @Mock
    private UserService userService;

    @Test
    public void test_Create() {
        long ownerId = 3L;
        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setPhone("0996783412");

        when(userService.readById(ownerId)).thenReturn(new User());
        when(candidateContactRepository.save(candidateContact)).thenReturn(candidateContact);

        CandidateContact actual = candidateContactService.create(ownerId, candidateContact);
        verify(candidateContactRepository, times(1)).save(candidateContact);

        Assertions.assertEquals(candidateContact, actual);
    }

    @Test
    public void test_ReadById() {
        long id = 2L;
        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setCandidateName("new name");

        when(candidateContactRepository.findById(id)).thenReturn(Optional.of(candidateContact));
        CandidateContact actual = candidateContactService.readById(id);

        verify(candidateContactRepository, times(1)).findById(id);

        assertEquals(candidateContact, actual);
    }

    @Test
    public void test_Update() {
        long id = 3L;
        User user1 = new User();
        user1.setId(2L);

        CandidateContact candidateContact = new CandidateContact();
        candidateContact.setEmail("new email");
        candidateContact.setId(id);
        candidateContact.setOwner(user1);
        candidateContact.setEmail("mail@mail.co");
        candidateContact.setCandidateName("Name of User");

        when(candidateContactRepository.findById(id)).thenReturn(Optional.of(candidateContact));
        when(candidateContactRepository.save(candidateContact)).thenReturn(candidateContact);

        CandidateContact actual = candidateContactService.update(id, candidateContact);
        verify(candidateContactRepository, times(1)).save(candidateContact);

        Assertions.assertEquals(candidateContact, actual);
    }

    @Test
    public void test_Delete() {
        when(candidateContactRepository.findById(5L)).thenReturn(Optional.of(new CandidateContact()));
        candidateContactService.delete(5L);

        verify(candidateContactRepository, times(1)).delete(new CandidateContact());
    }
}
