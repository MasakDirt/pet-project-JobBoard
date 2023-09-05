package com.board.job.service.authorization;

import com.board.job.model.entity.employer.EmployerProfile;
import com.board.job.service.employer.EmployerProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthEmployerProfileService {
    private final UserAuthService userAuthService;
    private final EmployerProfileService employerProfileService;

    public boolean isUsersSameByIdAndUserOwnerEmployerProfile(long userId, long employerId, String authEmail) {
        return userAuthService.isUsersSame(userId, authEmail) && getEmployer(employerId).getOwner().getId() == userId;
    }

    public boolean isUserAdminOrUsersSameByIdAndUserOwnerEmployerProfile(long userId, long employerId, String authEmail) {
        return userAuthService.isAdmin(authEmail) || userAuthService.isUsersSame(userId, authEmail) &&
                getEmployer(employerId).getOwner().getId() == userId;
    }

    public EmployerProfile getEmployer(long id) {
        return employerProfileService.readById(id);
    }
}
