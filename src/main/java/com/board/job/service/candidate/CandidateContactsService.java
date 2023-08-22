package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateContacts;
import com.board.job.repository.candidate.CandidateContactsRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CandidateContactsService {
    private final UserService userService;
    private final CandidateContactsRepository candidateContactsRepository;

    public CandidateContacts create(long ownerId, CandidateContacts candidateContacts) {
        candidateContacts.setOwner(userService.readById(ownerId));

        return candidateContactsRepository.save(candidateContacts);
    }

    public CandidateContacts readById(long id) {
        return candidateContactsRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Candidate not found"));
    }

    public CandidateContacts update(CandidateContacts updated) {
        readById(updated.getId());

        return updated;
    }

    public void delete(long id){
        candidateContactsRepository.delete(readById(id));
    }

    public CandidateContacts getWithPropertyPictureAttachedById(long id) {
        return candidateContactsRepository.findWithPropertyPictureAttachedById(id);
    }

    public void saveProfilePicture(long id, byte[] imageBytes) {
        var candidateContacts = readById(id);
        candidateContacts.setProfilePicture(imageBytes);

        candidateContactsRepository.save(candidateContacts);
    }
}
