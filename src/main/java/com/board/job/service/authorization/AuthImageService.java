package com.board.job.service.authorization;

import com.board.job.model.entity.Image;
import com.board.job.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthImageService {
    private final ImageService imageService;
    private final AuthEmployerProfileService authEmployerProfileService;
    private final AuthCandidateContactService authCandidateContactService;

    public boolean isUsersSameByIdAndUserOwnerCandidateContactsAndCandidateContactsContainImage(
            long userId, long candidateContactId, long id, String authEmail) {

        return authCandidateContactService
                .isUsersSameByIdAndUserOwnerCandidateContacts(userId, candidateContactId, authEmail)
                && getImage(id).getContact().getId() == candidateContactId;
    }

    public boolean isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerProfileContainImage(
            long userId, long employerId, long id, String authEmail) {

        return authEmployerProfileService
                .isUsersSameByIdAndUserOwnerEmployerProfile(userId, employerId, authEmail)
                && getImage(id).getProfile().getId() == employerId;
    }

    public Image getImage(long id) {
        return imageService.readById(id);
    }
}
