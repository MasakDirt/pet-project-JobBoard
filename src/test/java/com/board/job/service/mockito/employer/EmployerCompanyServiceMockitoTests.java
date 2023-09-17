package com.board.job.service.mockito.employer;

import com.board.job.model.entity.User;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.repository.employer.EmployerCompanyRepository;
import com.board.job.service.UserService;
import com.board.job.service.employer.EmployerCompanyService;
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
public class EmployerCompanyServiceMockitoTests {
    @InjectMocks
    private EmployerCompanyService employerCompanyService;
    @Mock
    private EmployerCompanyRepository employerCompanyRepository;
    @Mock
    private UserService userService;

    @Test
    public void test_Create() {
        long ownerID = 2L;
        EmployerCompany employerCompany = new EmployerCompany();
        employerCompany.setAboutCompany("About");
        employerCompany.setWebSite("web");

        when(userService.readById(ownerID)).thenReturn(new User());
        when(employerCompanyRepository.save(employerCompany)).thenReturn(employerCompany);
        EmployerCompany actual = employerCompanyService.create(ownerID, employerCompany);

        verify(employerCompanyRepository, times(1)).save(employerCompany);

        assertEquals(employerCompany, actual);
    }

    @Test
    public void test_ReadById() {
        long id = 2L;
        EmployerCompany employerCompany = new EmployerCompany();
        employerCompany.setAboutCompany("About");
        employerCompany.setWebSite("web");

        when(employerCompanyRepository.findById(id)).thenReturn(Optional.of(employerCompany));
        EmployerCompany actual = employerCompanyService.readById(id);

        verify(employerCompanyRepository, times(1)).findById(id);

        assertEquals(employerCompany, actual);
    }

    @Test
    public void test_Update() {
        long id = 3L;
        EmployerCompany employerCompany = new EmployerCompany();
        employerCompany.setAboutCompany("About");
        employerCompany.setWebSite("web");
        employerCompany.setId(id);

        when(employerCompanyRepository.findById(id)).thenReturn(Optional.of(employerCompany));
        when(employerCompanyRepository.save(employerCompany)).thenReturn(employerCompany);

        EmployerCompany actual = employerCompanyService.update(employerCompany);
        verify(employerCompanyRepository, times(1)).save(employerCompany);

        Assertions.assertEquals(employerCompany, actual);
    }

    @Test
    public void test_Delete() {
        when(employerCompanyRepository.findById(5L)).thenReturn(Optional.of(new EmployerCompany()));
        employerCompanyService.delete(5L);

        verify(employerCompanyRepository, times(1)).delete(new EmployerCompany());
    }
}
