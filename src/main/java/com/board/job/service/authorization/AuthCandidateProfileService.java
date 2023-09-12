package com.board.job.service.authorization;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.service.candidate.CandidateProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthCandidateProfileService {
    private final UserAuthService userAuthService;
    private final CandidateProfileService candidateProfileService;

    public boolean isUsersSameByIdAndUserOwnerCandidateProfile(long userId, long candidateId, String authEmail) {
        return userAuthService.isUsersSame(userId, authEmail) && getProfile(candidateId).getOwner().getId() == userId;
    }

    public boolean isUserAdminOrUsersSameByIdAndUserOwnerCandidateProfile(long userId, long candidateId, String authEmail) {
        return userAuthService.isAdmin(authEmail) || (userAuthService.isUsersSame(userId, authEmail)
                && getProfile(candidateId).getOwner().getId() == userId);
    }

    public CandidateProfile getProfile(long id) {
        return candidateProfileService.readById(id);
    }
}
