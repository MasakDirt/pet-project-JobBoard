package com.board.job.service.candidate;

import com.board.job.controller.HelperForPagesCollections;
import com.board.job.model.entity.candidate.CandidateProfile;
import com.board.job.repository.candidate.CandidateProfileRepository;
import com.board.job.service.UserService;
import com.board.job.service.VacancyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.*;

@Service
@AllArgsConstructor
public class CandidateProfileService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserService userService;

    public CandidateProfile create(long ownerId, CandidateProfile candidateProfile) {
        candidateProfile.setOwner(
                userService.addUserRole(ownerId, "CANDIDATE")
        );

        return candidateProfileRepository.save(candidateProfile);
    }

    public CandidateProfile readById(long id) {
        return candidateProfileRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("CandidateProfile not found"));
    }

    public CandidateProfile update(long id, CandidateProfile updated) {
        var old = readById(id);
        updated.setId(id);
        updated.setOwner(old.getOwner());
        updated.setMessengers(old.getMessengers());
        return candidateProfileRepository.save(updated);
    }

    public void delete(long id) {
        candidateProfileRepository.delete(readById(id));
    }

    public Page<CandidateProfile> getAllSorted(Pageable pageable, String searchText) {
        if (HelperForPagesCollections.checkSearchText(searchText)){
            return findBySearchText(pageable, searchText);
        }
        return candidateProfileRepository.findAll(pageable);
    }

    private Page<CandidateProfile> findBySearchText(Pageable pageable, String searchText) {
        return candidateProfileRepository.findAll()
                .stream()
                .filter(candidateProfile -> candidateProfile.getPosition().contains(searchText))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> new PageImpl<>(list, pageable, pageable.getPageSize())
                ));
    }
}
