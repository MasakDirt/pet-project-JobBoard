package com.board.job.service.authorization;

import com.board.job.model.entity.Feedback;
import com.board.job.service.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthFeedbackService {
    private final UserAuthService userAuthService;
    private final AuthCandidateProfileService candidateProfileService;
    private final AuthMessengerService authMessengerService;
    private final AuthEmployerProfileService employerProfileService;
    private final FeedbackService feedbackService;

    public boolean isUsersSameByIdAndUserOwnerCandidateProfileAndCandidateProfileContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback(
            long ownerId, long candidateId, long messengerForVacanciesReplyId, String id, String authEmail
    ) {
        var feedback = getFeedback(id);
        return userAuthService.isUsersSame(ownerId, authEmail)
                && candidateProfileService.getProfile(candidateId).getOwner().getId() == ownerId
                && authMessengerService.getMessenger(messengerForVacanciesReplyId).getCandidateProfile().getId() == candidateId
                && feedback.getMessengerForVacanciesReplyId() == messengerForVacanciesReplyId
                && feedback.getOwnerId() == ownerId;
    }

    public boolean isUsersSameByIdAndUserOwnerEmployerProfileAndEmployerOwnerVacancyAndVacancyContainMessengerAndMessengerContainFeedbackAndUserOwnerOfFeedback(
            long ownerId, long employerId, long vacancyId, long messengerForVacanciesReplyId, String id,  String authEmail
    ) {
        var messenger = authMessengerService.getMessenger(messengerForVacanciesReplyId);
        return userAuthService.isUsersSame(ownerId, authEmail)
                && employerProfileService.getEmployer(employerId).getOwner().getId() == ownerId
                && messenger.getVacancy().getEmployerProfile().getId() == employerId
                && messenger.getVacancy().getId() == vacancyId
                && getFeedback(id).getMessengerForVacanciesReplyId() == messengerForVacanciesReplyId;
    }

    private Feedback getFeedback(String id) {
        return feedbackService.readById(id);
    }
}
