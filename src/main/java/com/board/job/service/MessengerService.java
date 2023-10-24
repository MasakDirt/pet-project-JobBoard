package com.board.job.service;

import com.board.job.model.entity.Messenger;
import com.board.job.repository.MessengerRepository;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessengerService {
    private final MessengerRepository messengerRepository;
    private final VacancyService vacancyService;
    private final CandidateProfileService candidateProfileService;

    public Messenger create(long vacancyId, long candidateProfileId) {
        return messengerRepository.save(
                Messenger.of(
                        vacancyService.readById(vacancyId),
                        candidateProfileService.readById(candidateProfileId)
                )
        );
    }

    public Messenger readById(long id) {
        return messengerRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Messenger not found"));
    }

    public void delete(long id) {
        messengerRepository.delete(readById(id));
    }

    public List<Messenger> getAll() {
        return messengerRepository.findAll();
    }

    public List<Messenger> getAllByCandidateProfileId(long candidateProfileId) {
        return messengerRepository.findAllByCandidateProfileId(candidateProfileId);
    }

    public List<Messenger> getAllByVacancyId(long vacancyId) {
        return messengerRepository.findAllByVacancyId(vacancyId);
    }
}
