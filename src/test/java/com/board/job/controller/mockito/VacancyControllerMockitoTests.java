package com.board.job.controller.mockito;

import com.board.job.controller.VacancyController;
import com.board.job.model.dto.vacancy.CutVacancyResponse;
import com.board.job.model.dto.vacancy.FullVacancyResponse;
import com.board.job.model.dto.vacancy.VacancyRequest;
import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.model.entity.sample.Category;
import com.board.job.model.entity.sample.JobDomain;
import com.board.job.model.mapper.VacancyMapper;
import com.board.job.service.VacancyService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class VacancyControllerMockitoTests {
    @InjectMocks
    private VacancyController controller;
    @Mock
    private VacancyService service;
    @Mock
    private VacancyMapper mapper;
    @Mock
    private Authentication authentication;

    @Test
    public void test_GetAll() {
        when(service.getAll()).thenReturn(List.of(new Vacancy(), new Vacancy()));
        when(mapper.getCutVacancyResponseFromVacancy(new Vacancy()))
                .thenReturn(CutVacancyResponse.builder().build());
        controller.getAll(authentication);

        verify(service, times(1)).getAll();
    }

    @Test
    public void test_getSortedVacancies() {
        Sort sort = Sort.by(Sort.Direction.fromString("asc"), "id");
        Pageable pageable = PageRequest.of(1, 5, sort);

        when(service.getSorted(pageable)).thenReturn(Page.empty());
        controller.getSortedVacancies(new String[]{"id"}, "asc", 1, authentication);

        verify(service, times(1)).getSorted(pageable);
    }

    @Test
    public void test_GetById() {
        long ownerId = 34945L;
        long id = 345L;

        when(mapper.getFullVacancyResponseFromVacancy(new Vacancy())).thenReturn(FullVacancyResponse.builder().build());
        when(service.readById(id)).thenReturn(new Vacancy());
        controller.getById(ownerId, id, authentication);

        verify(service, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_getById() {
        long ownerId = 34945L;
        long id = 100L;
        when(service.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getById(ownerId, id, authentication));

        verify(service, times(1)).readById(id);
        verify(mapper, times(0)).getFullVacancyResponseFromVacancy(new Vacancy());
    }

    @Test
    public void test_GetAll_EmployerVacancies() {
        long employerID = 234L;
        when(service.getAllByEmployerProfileId(employerID)).thenReturn(List.of(new Vacancy(), new Vacancy()));
        when(mapper.getCutVacancyResponseFromVacancy(new Vacancy()))
                .thenReturn(CutVacancyResponse.builder().build());
        controller.getAllEmployerVacancies(2L, employerID, authentication);

        verify(service, times(1)).getAllByEmployerProfileId(employerID);
    }

    @Test
    public void test_GetEmployerVacancy() {
        long ownerId = 345L;
        long employerID = 234L;
        long id = 123L;

        when(mapper.getFullVacancyResponseFromVacancy(new Vacancy())).thenReturn(FullVacancyResponse.builder().build());
        when(service.readById(id)).thenReturn(new Vacancy());
        controller.getEmployerVacancy(ownerId, employerID, id, authentication);

        verify(service, times(1)).readById(id);
    }

    @Test
    public void test_NotFound_getEmployerVacancy() {
        long ownerId = 34945L;
        long employerID = 234L;
        long id = 100L;
        when(service.readById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> controller.getEmployerVacancy(ownerId, employerID, id, authentication));

        verify(service, times(1)).readById(id);
        verify(mapper, times(0)).getFullVacancyResponseFromVacancy(new Vacancy());
    }

    @Test
    public void test_Create() {
        long ownerId = 3L;
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setEmployerName("new NAme");

        Vacancy vacancy = new Vacancy();
        vacancy.setId(2L);
        vacancy.setDomain(JobDomain.DATING);
        vacancy.setCategory(Category.JAVA);
        vacancy.setEmployerProfile(employerProfile);

        when(mapper.getVacancyFromVacancyRequest(VacancyRequest.builder().build())).thenReturn(vacancy);
        when(service.create(ownerId, vacancy)).thenReturn(vacancy);
        controller.create(ownerId, 23, VacancyRequest.builder().build(), authentication);

        verify(service, times(1)).create(ownerId, vacancy);
    }

    @Test
    public void test_Update() {
        long ownerId = 3L;
        long id = 3L;
        EmployerProfile employerProfile = new EmployerProfile();
        employerProfile.setEmployerName("new NAme");

        Vacancy vacancy = new Vacancy();
        vacancy.setId(2L);
        vacancy.setDomain(JobDomain.DATING);
        vacancy.setCategory(Category.JAVA);
        vacancy.setEmployerProfile(employerProfile);

        when(mapper.getVacancyFromVacancyRequest(VacancyRequest.builder().build())).thenReturn(vacancy);
        when(service.update(id, vacancy)).thenReturn(vacancy);
        controller.update(ownerId, id, 23,
                VacancyRequest.builder().build(), authentication);

        verify(service, times(1)).update(id, vacancy);
    }

    @Test
    public void test_Delete() {
        long ownerId = 3L;
        long id = 3L;

        Vacancy vacancy = new Vacancy();
        vacancy.setId(2L);
        vacancy.setDomain(JobDomain.DATING);
        vacancy.setCategory(Category.JAVA);

        controller.delete(ownerId, id, 23, authentication);

        verify(service, times(1)).delete(id);
    }
}
