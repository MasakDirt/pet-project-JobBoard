package com.board.job.service.authorization;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.service.candidate.CandidateContactService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthCandidateContactService {
    private final UserAuthService userAuthService;
    private final CandidateContactService candidateContactService;

    public boolean isUsersSameByIdAndUserOwnerCandidateContacts(long userId, long candidateId, String authEmail) {
        return userAuthService.isUsersSame(userId, authEmail) && getContact(candidateId).getOwner().getId() == userId;
    }

    public boolean isUserAdminOrUsersSameByIdAndUserOwnerCandidateContacts(long userId, long candidateId, String authEmail) {
        return userAuthService.isAdmin(authEmail) || userAuthService.isUsersSame(userId, authEmail) &&
                getContact(candidateId).getOwner().getId() == userId;
    }

    public CandidateContact getContact(long id) {
        return candidateContactService.readById(id);
    }
}
