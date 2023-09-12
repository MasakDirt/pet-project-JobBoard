package com.board.job.service.employer;

import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.repository.employer.EmployerProfileRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmployerProfileService {
    private final UserService userService;
    private final EmployerProfileRepository employerProfileRepository;

    public EmployerProfile create(long ownerId, EmployerProfile employerProfile) {
        employerProfile.setOwnerWithName(
                userService.updateUserRolesAndGetUser(ownerId, "EMPLOYER")
        );
        return employerProfileRepository.save(employerProfile);
    }

    public EmployerProfile readById(long id) {
        return employerProfileRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Employer profile not found"));
    }

    public EmployerProfile update(long id, EmployerProfile updated) {
        var old = readById(id);
        updated.setId(id);
        updated.setImage(old.getImage());
        updated.setOwner(old.getOwner());
        updated.setVacancies(old.getVacancies());

        return employerProfileRepository.save(updated);
    }

    public void delete(long id) {
        employerProfileRepository.delete(readById(id));
    }
}
