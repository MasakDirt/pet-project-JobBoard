package com.board.job.repository;

import com.board.job.model.entity.Messenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessengerRepository  extends JpaRepository<Messenger, Long> {
    List<Messenger> findAllByCandidateProfileId(long candidateId);

    List<Messenger> findAllByVacancyId(long vacancyId);

    Optional<Messenger> findByCandidateProfile_OwnerIdAndVacancyId(long ownerId, long vacancyId);
    Optional<Messenger> findByVacancy_EmployerProfileIdAndCandidateProfileId(long employerProfileId, long candidateProfileId);
}
