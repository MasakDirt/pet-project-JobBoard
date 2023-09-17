package com.board.job.service.mockito.employer;

import com.board.job.model.entity.User;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.employer.EmployerProfileRepository;
import com.board.job.service.UserService;
import com.board.job.service.employer.EmployerProfileService;
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
public class EmployerProfileServiceMockitoTests {
    @InjectMocks
    private EmployerProfileService employerProfileService;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private UserService userService;


    @Test
    public void test_Create() {
        long ownerID = 2L;
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setLinkedInProfile("www.linkedin.co/dKLDJFDJkd");

        when(userService.updateUserRolesAndGetUser(ownerID, "EMPLOYER")).thenReturn(new User());
        when(employerProfileRepository.save(employerProfile)).thenReturn(employerProfile);
        EmployerProfile actual = employerProfileService.create(ownerID, employerProfile);

        verify(employerProfileRepository, times(1)).save(employerProfile);

        assertEquals(employerProfile, actual);
    }

    @Test
    public void test_ReadById() {
        long id = 2L;
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setLinkedInProfile("www.linkedin.co/dKLDJFDJkd");
        employerProfile.setPhone("none");
        employerProfile.setId(id);

        when(employerProfileRepository.findById(id)).thenReturn(Optional.of(employerProfile));
        EmployerProfile actual = employerProfileService.readById(id);

        verify(employerProfileRepository, times(1)).findById(id);

        assertEquals(employerProfile, actual);
    }

    @Test
    public void test_Update() {
        long id = 3L;
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setLinkedInProfile("www.linkedin.co/dKLDJFDJkd");
        employerProfile.setPhone("968798495355");
        employerProfile.setId(id);

        when(employerProfileRepository.findById(id)).thenReturn(Optional.of(employerProfile));
        when(employerProfileRepository.save(employerProfile)).thenReturn(employerProfile);

        EmployerProfile actual = employerProfileService.update(id, employerProfile);
        verify(employerProfileRepository, times(1)).save(employerProfile);

        Assertions.assertEquals(employerProfile, actual);
    }

    @Test
    public void test_Delete() {
        when(employerProfileRepository.findById(5L)).thenReturn(Optional.of(new EmployerProfile()));
        employerProfileService.delete(5L);

        verify(employerProfileRepository, times(1)).delete(new EmployerProfile());
    }
}
