package com.board.job.service.authorization;

import com.board.job.model.entity.User;
import com.board.job.model.entity.Vacancy;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthVacancyService {
    private final UserService userService;
    private final UserAuthService userAuthService;
    private final VacancyService vacancyService;

    public boolean isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileOwnerOfVacancy(
            long ownerId, long employerId, long id) {

        return getUser(ownerId).getEmployerProfile().getId() == employerId
                && getVacancy(id).getEmployerProfile().getId() == employerId;
    }

    public boolean isUsersSameAndEmployerProfileOwnerOfVacancy(long ownerId, long employerId, long id, String authEmail) {
        return userAuthService.isUsersSame(ownerId, authEmail)
                && getVacancy(id).getEmployerProfile().getId() == employerId;
    }

    public Vacancy getVacancy(long id) {
        return vacancyService.readById(id);
    }

    private User getUser(long id) {
        return userService.readById(id);
    }
}
