package com.board.job.service.authorization;

import com.board.job.model.entity.Vacancy;
import com.board.job.service.VacancyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthVacancyService {
    private final UserAuthService userAuthService;
    private final VacancyService vacancyService;

    public boolean isUsersSameAndOwnerOfVacancy(long ownerId, long id, String authEmail) {
        return userAuthService.isUsersSame(ownerId, authEmail)
                && getVacancy(id).getEmployerProfile().getOwner().getId() == id;
    }

    public Vacancy getVacancy(long id) {
        return vacancyService.readById(id);
    }
}
