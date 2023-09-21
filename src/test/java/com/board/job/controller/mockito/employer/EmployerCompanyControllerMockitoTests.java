package com.board.job.controller.mockito.employer;

import com.board.job.controller.employer.EmployerCompanyController;
import com.board.job.model.dto.employer_company.EmployerCompanyResponse;
import com.board.job.model.entity.User;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.mapper.employer.EmployerCompanyMapper;
import com.board.job.service.employer.EmployerCompanyService;
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
public class EmployerCompanyControllerMockitoTests {
    @InjectMocks
    private EmployerCompanyController controller;
    @Mock
    private EmployerCompanyService service;
    @Mock
    private EmployerCompanyMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_getById() {
        long id = 3L;
        EmployerCompany company = new EmployerCompany();
        company.setAboutCompany("About");
        company.setWebSite("web");

        when(mapper.getEmployerCompanyResponseFromEmployerCompany(company)).thenReturn(EmployerCompanyResponse.builder().build());
        when(service.readById(id)).thenReturn(company);
        controller.getById(3L, id, authentication);

        verify(service, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_getById() {
        long id = 100L;
        when(service.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getById(12, id, authentication));

        verify(service, times(1)).readById(id);
        verify(mapper, times(0)).getEmployerCompanyResponseFromEmployerCompany(new EmployerCompany());
    }

    @Test
    public void test_Create() {
        long ownerId = 3L;
        String about = "About";
        String web = "web";

        User user = new User();
        user.setFirstName("Ff");
        user.setLastName("Ll");

        EmployerCompany company = new EmployerCompany();
        company.setAboutCompany(about);
        company.setWebSite(web);
        company.setOwner(user);

        when(service.create(ownerId, about, web)).thenReturn(company);
        controller.create(ownerId,about, web, authentication);

        verify(service, times(1)).create(ownerId, about, web);
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

        EmployerCompany updated = new EmployerCompany();
        updated.setAboutCompany(about);
        updated.setWebSite(web);
        updated.setOwner(user);


        when(service.update(id, new EmployerCompany(about, web))).thenReturn(updated);
        controller.update(ownerId, id, about, web, authentication);

        verify(service, times(1)).update(id, new EmployerCompany(about, web));
    }

    @Test
    public void test_Delete() {
        long ownerId = 3L;
        long id = 3L;

        User user = new User();
        user.setFirstName("Ff");
        user.setLastName("Ll");

        EmployerCompany company = new EmployerCompany();
        company.setAboutCompany("About");
        company.setWebSite("web");
        company.setOwner(user);

        when(service.readById(id)).thenReturn(company);
        controller.delete(ownerId, id, authentication);

        verify(service, times(1)).delete(id);
    }
}
