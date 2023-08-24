package com.board.job.service;

import com.board.job.model.entity.Vacancy;
import com.board.job.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final UserService userService;

    public Vacancy create(long ownerId, Vacancy vacancy) {
        vacancy.setEmployerCompany(userService.getEmployerData(ownerId).left);
        vacancy.setEmployerProfile(userService.getEmployerData(ownerId).right);

        return vacancyRepository.save(vacancy);
    }

    public Vacancy readById(long id) {
        return vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Vacancy not found"));
    }

    public Vacancy update(Vacancy updated) {
        readById(updated.getId());

        return vacancyRepository.save(updated);
    }

    public void delete(long id) {
        vacancyRepository.delete(readById(id));
    }

    public List<Vacancy> getAll() {
        return vacancyRepository.findAll();
    }
}
