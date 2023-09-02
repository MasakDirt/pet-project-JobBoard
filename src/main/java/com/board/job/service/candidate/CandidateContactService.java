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
        candidateContact.setOwnerWithFields(userService.readById(ownerId));
        return candidateContactRepository.save(candidateContact);
    }

    public CandidateContact readById(long id) {
        return candidateContactRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Candidate not found"));
    }

    public CandidateContact update(CandidateContact updated) {
        readById(updated.getId());

        return candidateContactRepository.save(updated);
    }

    public void delete(long id) {
        candidateContactRepository.delete(readById(id));
    }
}
