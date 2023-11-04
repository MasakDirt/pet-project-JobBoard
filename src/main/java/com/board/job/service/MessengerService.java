package com.board.job.service;

import com.board.job.model.entity.MessengerForVacanciesReply;
import com.board.job.repository.MessengerRepository;
import com.board.job.service.candidate.CandidateProfileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessengerService {
    //    ---------------- VACANCIES REPLY ---------------
    private final MessengerRepository messengerRepository;
    private final VacancyService vacancyService;
    private final CandidateProfileService candidateProfileService;

    public MessengerForVacanciesReply create(long vacancyId, long candidateProfileId) {
        return messengerRepository.save(
                MessengerForVacanciesReply.of(
                        vacancyService.readById(vacancyId),
                        candidateProfileService.readById(candidateProfileId)
                )
        );
    }

    public MessengerForVacanciesReply readById(long id) {
        return messengerRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Messenger not found"));
    }

    public Optional<MessengerForVacanciesReply> readByOwnerAndVacancy(long ownerId, long vacancyId) {
        return messengerRepository.findByCandidateProfile_OwnerIdAndVacancyId(ownerId, vacancyId);
    }

    public Optional<MessengerForVacanciesReply> readByEmployerProfileIdAndCandidateProfileId(long employerProfileId, long candidateProfileId) {
        return messengerRepository.findByVacancy_EmployerProfileIdAndCandidateProfileId(employerProfileId, candidateProfileId);
    }

    public void delete(long id) {
        messengerRepository.delete(readById(id));
    }

    public List<MessengerForVacanciesReply> getAll() {
        return messengerRepository.findAll();
    }

    public List<MessengerForVacanciesReply> getAllByCandidateProfileId(long candidateProfileId) {
        return messengerRepository.findAllByCandidateProfileId(candidateProfileId);
    }

    public List<MessengerForVacanciesReply> getAllByVacancyId(long vacancyId) {
        return messengerRepository.findAllByVacancyId(vacancyId);
    }
}
