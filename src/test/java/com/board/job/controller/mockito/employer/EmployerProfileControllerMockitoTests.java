package com.board.job.controller.mockito.employer;

import com.board.job.controller.employer.EmployerProfileController;
import com.board.job.model.dto.employer_profile.EmployerProfileRequest;
import com.board.job.model.dto.employer_profile.EmployerProfileResponse;
import com.board.job.model.entity.User;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.mapper.employer.EmployerProfileMapper;
import com.board.job.service.employer.EmployerProfileService;
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
public class EmployerProfileControllerMockitoTests {
    @InjectMocks
    private EmployerProfileController controller;
    @Mock
    private EmployerProfileService service;
    @Mock
    private EmployerProfileMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_getById() {
        long id = 3L;
        EmployerProfile profile = new EmployerProfile();
        profile.setTelegram("@tele");
        profile.setCompanyName("Company");

        when(mapper.getEmployerProfileResponseFromEmployerProfile(profile)).thenReturn(EmployerProfileResponse.builder().build());
        when(service.readById(id)).thenReturn(profile);
        controller.getById(3L, id, authentication);

        verify(service, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_getById() {
        long id = 100L;
        when(service.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getById(12, id, authentication));

        verify(service, times(1)).readById(id);
        verify(mapper, times(0)).getEmployerProfileResponseFromEmployerProfile(new EmployerProfile());
    }

    @Test
    public void test_Create() {
        long ownerId = 3L;
        String about = "About";
        String web = "web";

        User user = new User();
        user.setFirstName("Ff");
        user.setLastName("Ll");

        EmployerProfile profile = new EmployerProfile();
        profile.setTelegram("@tele");
        profile.setCompanyName("Company");
        profile.setOwner(user);

        when(service.create(ownerId, profile)).thenReturn(profile);
        when(mapper.getEmployerProfileFromEmployerProfileRequest(EmployerProfileRequest.builder().build())).thenReturn(profile);
        controller.create(ownerId, EmployerProfileRequest.builder().build(), authentication);

        verify(service, times(1)).create(ownerId, profile);
    }

    @Test
    public void test_Update() {
        long ownerId = 3L;
        long id = 3L;
        String about = "Updated";
        String web = "new";

        User user = new User();
        user.setFirstName("Ff");
        user.setLastName("Ll");

        EmployerProfile profile = new EmployerProfile();
        profile.setTelegram("@tele");
        profile.setCompanyName("Company");
        profile.setOwner(user);


        when(mapper.getEmployerProfileFromEmployerProfileRequest(EmployerProfileRequest.builder().build())).thenReturn(profile);
        when(service.update(id, profile)).thenReturn(profile);
        controller.update(ownerId, id, EmployerProfileRequest.builder().build(), authentication);

        verify(service, times(1)).update(id, profile);
    }

    @Test
    public void test_Delete() {
        long ownerId = 3L;
        long id = 3L;

        User user = new User();
        user.setFirstName("Ff");
        user.setLastName("Ll");

        EmployerProfile profile = new EmployerProfile();
        profile.setTelegram("@tele");
        profile.setCompanyName("Company");
        profile.setOwner(user);

        when(service.readById(id)).thenReturn(profile);
        controller.delete(ownerId, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
