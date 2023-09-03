package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateContact;
import com.board.job.repository.candidate.CandidateContactRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CandidateContactService {
    private final UserService userService;
    private final CandidateContactRepository candidateContactRepository;

    public CandidateContact create(long ownerId, CandidateContact candidateContact) {
        var owner = userService.readById(ownerId);
        candidateContact.setOwner(owner);
        var candidateName = candidateContact.getCandidateName();
        var candidateEmail = candidateContact.getEmail();

        if (candidateName == null || candidateName.trim().isEmpty())
            candidateContact.setCandidateName(owner.getName());

        if (candidateEmail == null || candidateEmail.trim().isEmpty())
            candidateContact.setEmail(owner.getEmail());

        return candidateContactRepository.save(candidateContact);
    }

    public CandidateContact readById(long id) {
        return candidateContactRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Candidate not found"));
    }

    public CandidateContact update(long id, CandidateContact updated) {
        updated.setId(id);
        return create(readById(id).getOwner().getId(), updated);
    }

    public void delete(long id) {
        candidateContactRepository.delete(readById(id));
    }
}
