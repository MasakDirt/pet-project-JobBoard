package com.board.job.repository;

import com.board.job.model.entity.MessengerForVacanciesReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessengerRepository  extends JpaRepository<MessengerForVacanciesReply, Long> {
    List<MessengerForVacanciesReply> findAllByCandidateProfileId(long candidateId);

    List<MessengerForVacanciesReply> findAllByVacancyId(long vacancyId);

    Optional<MessengerForVacanciesReply> findByCandidateProfile_OwnerIdAndVacancyId(long ownerId, long vacancyId);
    Optional<MessengerForVacanciesReply> findByVacancy_EmployerProfileIdAndCandidateProfileId(long employerProfileId, long candidateProfileId);
}
