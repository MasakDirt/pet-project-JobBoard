package com.board.job.service.authorization;

import com.board.job.model.entity.employer.EmployerCompany;
import com.board.job.service.employer.EmployerCompanyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthEmployerCompanyService {
    private final UserAuthService userAuthService;
    private final EmployerCompanyService employerCompanyService;

    public boolean isUsersSameByIdAndUserOwnerEmployerCompany(long userId, long companyId, String authEmail) {
        return userAuthService.isUsersSame(userId, authEmail) && getCompany(companyId).getOwner().getId() == userId;
    }

    public boolean isUserAdminOrUsersSameByIdAndUserOwnerEmployerCompany(long userId, long companyId, String authEmail) {
        return userAuthService.isAdmin(authEmail) || userAuthService.isUsersSame(userId, authEmail) &&
                getCompany(companyId).getOwner().getId() == userId;
    }

    public EmployerCompany getCompany(long id) {
        return employerCompanyService.readById(id);
    }
}
