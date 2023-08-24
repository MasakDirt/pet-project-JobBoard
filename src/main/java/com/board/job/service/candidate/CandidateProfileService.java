package com.board.job.service.candidate;

import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.repository.candidate.CandidateProfileRepository;
import com.board.job.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CandidateProfileService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserService userService;

    public CandidateProfile create(long ownerId, CandidateProfile candidateProfile) {
        candidateProfile.setOwner(
                userService.updateUserRolesAndGetUser(ownerId, "CANDIDATE")
        );

        return candidateProfileRepository.save(candidateProfile);
    }

    public CandidateProfile readById(long id) {
        return candidateProfileRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("CandidateProfile not found"));
    }

    public CandidateProfile update(CandidateProfile updated) {
        readById(updated.getId());

        return candidateProfileRepository.save(updated);
    }

    public void delete(long id) {
        candidateProfileRepository.delete(readById(id));
    }

    public List<CandidateProfile> getAll() {
        return candidateProfileRepository.findAll();
    }
}
