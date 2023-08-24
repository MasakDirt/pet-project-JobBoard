package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.repository.employer.EmployerCompanyRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployerCompanyService {
    private final UserService userService;
    private final EmployerCompanyRepository employerCompanyRepository;

    public EmployerCompany create(long ownerId, EmployerCompany employerCompany) {
        employerCompany.setOwner(userService.readById(ownerId));
        return employerCompanyRepository.save(employerCompany);
    }

    public EmployerCompany readById(long id) {
        return employerCompanyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Employer company not found!"));
    }

    public EmployerCompany update(EmployerCompany updated) {
        readById(updated.getId());

        return employerCompanyRepository.save(updated);
    }

    public void delete(long id) {
        employerCompanyRepository.delete(readById(id));
    }
}
