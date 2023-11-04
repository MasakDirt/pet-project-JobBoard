package com.board.job.service;

import com.board.job.model.entity.MessengerForEmployersReply;
import com.board.job.repository.MessengerForEmployersReplyRepository;
import com.board.job.service.candidate.CandidateProfileService;
import com.board.job.service.employer.EmployerProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessengerForEmployersReplyService {
    private final EmployerProfileService employerProfileService;
    private final CandidateProfileService candidateProfileService;
    private final MessengerForEmployersReplyRepository messengerForEmployersReplyRepository;

    public MessengerForEmployersReply create(long employerProfileId, long candidateProfileId) {
        return messengerForEmployersReplyRepository.save(
                MessengerForEmployersReply.of(
                        employerProfileService.readById(employerProfileId),
                        candidateProfileService.readById(candidateProfileId)
                )
        );
    }

    public MessengerForEmployersReply readById(long id) {
        return messengerForEmployersReplyRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Messenger not found"));
    }

    public Optional<MessengerForEmployersReply> readByEmployerProfileAndCandidateProfile(long employerProfileId, long candidateProfileId) {
        return messengerForEmployersReplyRepository.findByEmployerProfileIdAndCandidateProfileId(employerProfileId, candidateProfileId);
    }

    public void deleteEmployerReplyMessenger(long id) {
        messengerForEmployersReplyRepository.delete(readById(id));
    }


    public List<MessengerForEmployersReply> getAll() {
        return messengerForEmployersReplyRepository.findAll();
    }

    public List<MessengerForEmployersReply> getAllByCandidateProfileId(long candidateProfileId) {
        return messengerForEmployersReplyRepository.findAllByCandidateProfileId(candidateProfileId);
    }

    public List<MessengerForEmployersReply> getAllByEmployerProfileId(long employerProfileId) {
        return messengerForEmployersReplyRepository.findAllByEmployerProfileId(employerProfileId);
    }
}
