package com.board.job.service.mockito;

import com.board.job.model.entity.Vacancy;
import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.VacancyRepository;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import org.apache.cassandra.utils.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class VacancyServiceMockitoTests {
    @InjectMocks
    private VacancyService vacancyService;
    @Mock
    private UserService userService;
    @Mock
    private VacancyRepository vacancyRepository;

    @Test
    public void test_Create() {
        when(userService.getEmployerData(2L)).thenReturn(Pair.create(new EmployerCompany(), new EmployerProfile()));
        vacancyService.create(2L, new Vacancy());

        verify(vacancyRepository, times(1)).save(new Vacancy());
    }

    @Test
    public void test_ReadById() {
        long id = 3L;

        when(vacancyRepository.findById(id)).thenReturn(Optional.of(new Vacancy()));
        vacancyService.readById(id);

        verify(vacancyRepository, times(1)).findById(id);
    }

    @Test
    public void test_Update() {
        when(vacancyRepository.findById(0L)).thenReturn(Optional.of(new Vacancy()));
        vacancyService.update(new Vacancy());

        verify(vacancyRepository, times(1)).save(new Vacancy());
    }

    @Test
    public void test_Delete() {
        when(vacancyRepository.findById(3L)).thenReturn(Optional.of(new Vacancy()));
        vacancyService.delete(3L);

        verify(vacancyRepository, times(1)).delete(new Vacancy());
    }

    @Test
    public void test_GetAll() {
        vacancyService.getAll();

        verify(vacancyRepository, times(1)).findAll();
    }
}
