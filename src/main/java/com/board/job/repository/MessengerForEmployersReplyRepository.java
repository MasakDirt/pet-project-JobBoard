package com.board.job.repository;

import com.board.job.model.entity.MessengerForEmployersReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessengerForEmployersReplyRepository extends JpaRepository<MessengerForEmployersReply, Long> {
    List<MessengerForEmployersReply> findAllByCandidateProfileId(long candidateId);

    List<MessengerForEmployersReply> findAllByEmployerProfileId(long employerProfileId);
    Optional<MessengerForEmployersReply> findByEmployerProfileIdAndCandidateProfileId(long employerProfileId, long candidateProfileId);
}
