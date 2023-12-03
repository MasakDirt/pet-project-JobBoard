package com.board.job.service;

import com.board.job.model.entity.Vacancy;
import com.board.job.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.board.job.controller.ControllerHelper.checkSearchText;

@Service
@AllArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final UserService userService;

    public Vacancy create(long ownerId, Vacancy vacancy) {
        var employerData = userService.getEmployerData(ownerId);

        vacancy.setEmployerCompany(employerData.left);
        vacancy.setEmployerProfile(employerData.right);

        return vacancyRepository.save(vacancy);
    }

    public Vacancy readById(long id) {
        return vacancyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Vacancy not found"));
    }

    public Vacancy update(long id, Vacancy updated) {
        var vacancy = readById(id);
        updated.setId(id);
        updated.setEmployerCompany(vacancy.getEmployerCompany());
        updated.setEmployerProfile(vacancy.getEmployerProfile());
        updated.setPostedAt(vacancy.getPostedAt());

        return vacancyRepository.save(updated);
    }

    public void delete(long id) {
        vacancyRepository.delete(readById(id));
    }

    public List<Vacancy> getAll() {
        return vacancyRepository.findAll();
    }

    public Page<Vacancy> getSortedVacancies(Pageable pageable, String searchText) {
        if (checkSearchText(searchText)) {
            return findBySearchText(pageable, searchText);
        }

        return vacancyRepository.findAll(pageable);
    }

    private Page<Vacancy> findBySearchText(Pageable pageable, String searchText) {
        return vacancyRepository.findAll()
                .stream()
                .filter(vacancy -> vacancy.getLookingFor().contains(searchText))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new PageImpl<>(list, pageable, pageable.getPageSize())
                ));
    }

    public List<Vacancy> getAllByEmployerProfileId(long employerId) {
        return vacancyRepository.getVacanciesByEmployerProfileId(employerId);
    }
}
