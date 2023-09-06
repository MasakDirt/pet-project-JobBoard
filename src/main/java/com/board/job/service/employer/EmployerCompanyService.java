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

    public EmployerCompany create(long ownerId, String aboutCompany, String webSite) {

        var employerCompany = new EmployerCompany(aboutCompany, webSite);
        employerCompany.setOwner(userService.readById(ownerId));

        return employerCompanyRepository.save(employerCompany);
    }

    public EmployerCompany readById(long id) {
        return employerCompanyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Employer company not found!"));
    }

    public EmployerCompany update(long id, EmployerCompany updated) {
        var oldCompany = readById(id);
        oldCompany.setAboutCompany(updated.getAboutCompany());
        oldCompany.setWebSite(updated.getWebSite());

        return employerCompanyRepository.save(oldCompany);
    }

    public void delete(long id) {
        employerCompanyRepository.delete(readById(id));
    }
}
