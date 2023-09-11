package com.board.job.service.authorization;

import com.board.job.model.entity.Messenger;
import com.board.job.service.MessengerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthMessengerService {
    private final UserAuthService userAuthService;
    private final AuthCandidateProfileService candidateProfileService;
    private final AuthEmployerProfileService employerProfileService;
    private final MessengerService messengerService;

    public boolean isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger(
            long ownerId, long candidateId, long id, String authEmail
    ) {
        return userAuthService.isAdmin(authEmail) ||
                (userAuthService.isUsersSame(ownerId, authEmail)
                        && candidateProfileService.getProfile(candidateId).getOwner().getId() == ownerId
                        && getMessenger(id).getCandidate().getId() == candidateId);
    }

    public boolean isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessenger(
            long ownerId, long candidateId, long id, String authEmail
    ) {
        return userAuthService.isUsersSame(ownerId, authEmail)
                        && candidateProfileService.getProfile(candidateId).getOwner().getId() == ownerId
                        && getMessenger(id).getCandidate().getId() == candidateId;
    }

    public boolean isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessenger(
            long ownerId, long employerId, long vacancyId, long id, String authEmail
    ) {
        var messenger = getMessenger(id);
        return userAuthService.isUsersSame(ownerId, authEmail)
                        && employerProfileService.getEmployer(employerId).getOwner().getId() == ownerId
                        && messenger.getVacancy().getEmployerProfile().getId() == employerId
                        && messenger.getVacancy().getId() == vacancyId;
    }

    public Messenger getMessenger(long id) {
        return messengerService.readById(id);
    }
}
